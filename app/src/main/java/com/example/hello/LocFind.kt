package com.example.hello

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hello.adapter.ListAdapter
import com.example.hello.adapter.ListLayout
import com.example.hello.api.KakaoAPI
import com.example.hello.databinding.KakaoMapBinding
import com.example.hello.model.ResultSearchKeyword
import kotlinx.android.synthetic.main.balloon_layout.*
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LocFind : AppCompatActivity() {
    private lateinit var binding : KakaoMapBinding
    private val listItems = arrayListOf<ListLayout>()   // 리사이클러 뷰 아이템
    private var pageNumber = 1 // 검색 페이지 번호
    private var keyword = ""   // 검색 키워드
    private val listAdapter = ListAdapter(
        onClickSelect = {
            selectLoc(it)
        }
    ).build(listItems)
    private val eventListener = MarkerEventListener(this)

    fun selectLoc(loc: ListLayout) {
        val returnIntent = Intent(this, Course::class.java)
        val bundle = Bundle()

        bundle.putString("id", loc.id)
        bundle.putString("name", loc.name)
        returnIntent.putExtras(bundle)
        setResult(RESULT_OK, returnIntent)

        finish()
    }

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK af1be7ad53a6d66f0698eca0997083a2"  // REST API 키
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = KakaoMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        keyword += intent.getStringExtra("place")
        val name = intent.getStringExtra("locName")
        if(name != ""){
            keyword += name
        }
        searchKeyword(keyword, pageNumber)

        // 리사이클러 뷰
        binding.searchList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.searchList.adapter = listAdapter

        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                binding.mapView.setMapCenterPointAndZoomLevel(mapPoint, 5, true)
                binding.mapView.setCalloutBalloonAdapter(CustomBalloonAdapter(layoutInflater))
                binding.mapView.setPOIItemEventListener(eventListener)
            }
        })

        // 검색 버튼
        binding.btnSearch.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.searchField.windowToken, 0)

            keyword = intent.getStringExtra("place") + " " + binding.searchField.text.toString()
            searchKeyword(keyword, pageNumber)
        }

        binding.btnPrevPage.setOnClickListener {
            pageNumber--
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }

        // 다음 페이지 버튼
        binding.btnNextPage.setOnClickListener {
            pageNumber++
            binding.tvPageNumber.text = pageNumber.toString()
            searchKeyword(keyword, pageNumber)
        }
    }

    // 키워드 검색 함수
    private fun searchKeyword(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()    // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)    // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword, page)    // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<ResultSearchKeyword> {
            override fun onResponse(call: Call<ResultSearchKeyword>, response: Response<ResultSearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 처리 함수
    @SuppressLint("NotifyDataSetChanged")
    private fun addItemsAndMarkers(searchResult: ResultSearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            binding.mapView.removeAllPOIItems()
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = ListLayout(document.id,
                    document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    userObject = arrayOf<String>(document.id, document.address_name)
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                }
                binding.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
            binding.btnPrevPage.isEnabled = pageNumber != 1 // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)
        val address: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_address)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            name.text = poiItem?.itemName
//            address.text = poiItem?.userObject[]
            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            return mCalloutBalloon
        }
    }

    class MarkerEventListener(val context: Context): MapView.POIItemEventListener{
        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

        }

        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

        }

        override fun onCalloutBalloonOfPOIItemTouched(
            mapView: MapView?,
            poiItem: MapPOIItem?,
            buttonType: MapPOIItem.CalloutBalloonButtonType?
        ) {
//            val returnIntent = Intent(context, Course::class.java)
//            val bundle = Bundle()
//
//            bundle.putString("id", )
//            bundle.putString("name", poiItem.itemName)
//            returnIntent.putExtras(bundle)
//            setResult(RESULT_OK, returnIntent)
//
//            finish()
        }
    }
}