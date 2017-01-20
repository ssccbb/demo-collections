package com.sung.demo.democollections.AMapGaode;

import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.sung.demo.democollections.R;

public class AMapLocation extends AppCompatActivity implements LocationSource, AMapLocationListener,
        AMap.OnCameraChangeListener,AMap.OnMapClickListener, AMap.OnMarkerClickListener,
        GeocodeSearch.OnGeocodeSearchListener,SensorEventListener {
    private String TAG="AMapLocation";
    private MapView mMapView;

    //地理编码
    private GeocodeSearch geocoderSearch;
    private String addressName;
    private SensorManager sm;

    //location params
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amapgd_amap_location);
        initAMap(savedInstanceState);
    }

    private void initAMap(Bundle savedInstanceState){
        Log.e(TAG, "onCreate: "+AMapUtils.sHA1(this));
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        AMap aMap = mMapView.getMap();
        initLocation(aMap);
        initGeocodeSearch();
    }

    private void initGeocodeSearch(){
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initLocation(AMap aMap){
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AMapConstants.BEIJING,16));
        // 设置定位监听
        aMap.setLocationSource(this);
        aMap.setOnCameraChangeListener(this);
        // 设置地图的点击监听
        aMap.setOnMapClickListener(this);
        // 设置标记的点击监听
        aMap.setOnMarkerClickListener(this);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        UiSettings aMapUiSettings = aMap.getUiSettings();
        aMapUiSettings.setTiltGesturesEnabled(false);// 设置地图是否可以倾斜
        aMapUiSettings.setScaleControlsEnabled(true);// 设置地图默认的比例尺是否显示
        aMapUiSettings.setCompassEnabled(true);//是否显示指南针
        aMapUiSettings.setMyLocationButtonEnabled(true); // 是否显示默认的定位按钮
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sm.unregisterListener(this);
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            //初始化定位
            mlocationClient = new AMapLocationClient(this);
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位回调监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();//启动定位
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 定位成功后回调函数
     */
    boolean toast=true;
    float ROTATE_ANGLE=0;
    @Override
    public void onLocationChanged(com.amap.api.location.AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                if (toast) {
                    new AlertDialog.Builder(this).setMessage(errText).show();
                    toast=!toast;
                }
            }
        }
    }

    boolean toast1=true;
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        LatLng target = cameraPosition.target;
        Log.e(TAG, "onCameraChange: "+target.latitude + "jinjin------" + target.longitude );
        if (toast1) {
            new AlertDialog.Builder(this).setMessage("onCameraChange: "+target.latitude + "jinjin------" + target.longitude).show();
            toast1=!toast1;
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    /*
    * 地图的点击事件
    * */
    @Override
    public void onMapClick(LatLng latLng) {
        markBuilder(latLng);
    }

    /*
    * 创建标记
    * */
    private boolean build_tag=false;
    private void markBuilder(final LatLng latLng){
        //根据坐标查询地理描述
        addressName=null;
        LatLonPoint latLonPoint=new LatLonPoint(latLng.latitude,latLng.longitude);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

        //创建
        final Marker marker = mMapView.getMap().addMarker(new MarkerOptions().
                position(latLng).
                title(latLng.latitude + "," + latLng.longitude).
                snippet(latLng.latitude + "," + latLng.longitude));
        /*Animation animation = new RotateAnimation(marker.getRotateAngle(), marker.getRotateAngle() + 180, 0, 0, 0);
        long duration = 1000L;
        animation.setDuration(duration);
        animation.setInterpolator(new LinearInterpolator());
        marker.setAnimation(animation);
        marker.startAnimation();*/

        //弹窗
        new AlertDialog.Builder(this).setMessage("坐标："+latLng.latitude + "," + latLng.longitude+"，创建此处标记？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                marker.setVisible(false);
            }
        }).setPositiveButton("创建", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                build_tag=true;
                Toast.makeText(AMapLocation.this, latLng.latitude + "," + latLng.longitude, Toast.LENGTH_SHORT).show();
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (!build_tag)
                    marker.setVisible(false);
            }
        }).show();
    }

    /*
    * 标记的点击事件
    * */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        LatLng position = marker.getOptions().getPosition();
        new AlertDialog.Builder(this).setMessage("Title:"+marker.getTitle()
                +"\nSnippet:"+marker.getSnippet()
                +"\nLatLng"+position.latitude+","+position.longitude).setPositiveButton("取消标记", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                marker.setVisible(false);
            }
        }).show();
        return true;
    }

    /*
    * 逆地理编码（坐标转地址）
    * 用来查询地址
    * */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        addressName=null;
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = result.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                Toast.makeText(this, addressName, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "查询失败！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "查询失败！", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    * 地理编码（地址转坐标）
    * */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    float olderDegress=0;
    float orientation_degress=0;//指南针传感器的角度
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()){
            case Sensor.TYPE_ORIENTATION:
                orientation_degress = event.values[0];
                Log.e(TAG, "onSensorChanged: "+orientation_degress);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                float[] values = event.values;
                float ax = values[0];
                float ay = values[1];

                double g = Math.sqrt(ax * ax + ay * ay);
                double cos = ay / g;
                if (cos > 1) {
                    cos = 1;
                } else if (cos < -1) {
                    cos = -1;
                }
                double rad = Math.acos(cos);
                if (ax < 0) {
                    rad = 2 * Math.PI - rad;
                }

                int uiRot = getWindowManager().getDefaultDisplay().getRotation();
                double uiRad = Math.PI / 2 * uiRot;
                rad -= uiRad;

                float accelerometer_degress = (float) (180 * rad / Math.PI);
                float bearing = mMapView.getMap().getCameraPosition().bearing;
                Log.e(TAG, "onSensorChanged: "+accelerometer_degress+"/"+bearing+"/"+olderDegress);

                float differ = olderDegress - accelerometer_degress;
                if ((differ>(-20)&&differ<20)) {
                    mMapView.getMap().setMyLocationRotateAngle(accelerometer_degress + bearing);// 设置小蓝点旋转角度
                    Log.e(TAG, "onSensorChanged: skip");
                }
                olderDegress=accelerometer_degress;
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
