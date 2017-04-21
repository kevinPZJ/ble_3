package com.example.kevin.health.Ui.health;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kevin.health.Database.HealthData;
import com.example.kevin.health.R;

import com.example.kevin.health.base.BaseFragment;
import com.example.kevin.health.databinding.FragmentHealthBinding;
import com.example.kevin.health.utils.Tools;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * Created by hyx on 2017/2/6.
 */

public class HealFragment extends BaseFragment implements HealthContract.View{


    private List<HealthData> list; // 数据

    private FloatingActionButton fab;
    private LineChartView lineChartStep;
    private ColumnChartView distanceColumnChart;
    private ColumnChartView calColumnChart;

    private TextView tvStep;
    private TextView tvDistance;
    private TextView tvCal;
    private TextView tvSleepHour;


    private ColumnChartData data;
    /**
     * Deep copy of data.
     */
    private ColumnChartData DistanceColumnChartData;  //  距离的表格数据

    private ColumnChartData CalColumnChartData;  // 热量的表格数据

    private String chooseDate;



    private List<AxisValue>  Step_X = new ArrayList<AxisValue>();   //Step_X坐标


    private int mYear = Calendar.getInstance().get(Calendar.YEAR);
    private int mMonth = Calendar.getInstance().get(Calendar.MONTH);
    private int mDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

    private boolean hasAxes = true;
    private boolean hasAxesNames = true;

    public static HealFragment newInstance() {
        HealFragment fragment = new HealFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        FragmentHealthBinding  binding  = DataBindingUtil.inflate(inflater, R.layout.fragment_health,container, false);
        fab=binding.fab;
        lineChartStep=binding.lineChartStep;
        distanceColumnChart=binding.distanceColumnChart;
        calColumnChart=binding.calColumnChart;
        tvStep=binding.tvStep;
        tvDistance=binding.tvDistance;
        tvCal=binding.tvCal;
        tvSleepHour=binding.tvSleep;
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list = new ArrayList<>();
        list.add(new HealthData(9, 7841, 324, 4.81, "20170418", 6.6, 6));
        list.add(new HealthData(8, 6541, 284, 3.81, "20170417", 6.6, 6));
        list.add(new HealthData(7, 6541, 294, 3.38, "20170416", 6.6, 6));
        list.add(new HealthData(6, 4541, 274, 2.81, "20170415", 6.6, 6));
        list.add(new HealthData(5, 9241, 224, 4.11, "20170414", 6.6, 6));
        list.add(new HealthData(4, 1441, 194, 1.81, "20170413", 6.6, 6));
        list.add(new HealthData(3, 7841, 324, 2.81, "20170412", 6.6, 6));
        list.add(new HealthData(2, 8041, 354, 2.93, "20170411", 6.6, 6));
        list.add(new HealthData(1, 6341, 224, 2.02, "20170409", 6.6, 6));



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                now.set(mYear, mMonth, mDay);
                DatePickerDialog dialog = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        Calendar temp = Calendar.getInstance();
                        temp.clear();
                        temp.set(year, monthOfYear, dayOfMonth);

                        showToast(year+"年"+(mMonth+1)+"月"+mDay+"日");

                        String Month = null ;
                        if (mMonth< 10 ){
                            Month = "0"+ String.valueOf(mMonth+1);
                        } else {
                            Month =  String.valueOf(mMonth+1);
                        }
                        String Day = null ;
                        if (mDay< 10 ){
                            Day = "0"+ String.valueOf(mDay);
                        } else {
                            Day =  String.valueOf(mDay);
                        }


                            chooseDate = String.valueOf(mYear) +Month +Day;

                        Log.e("chooseDate",chooseDate+"");
                        showChooseData(chooseDate);



                    }
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
                dialog.setMaxDate(Calendar.getInstance());
                Calendar minDate = Calendar.getInstance();
                // 2013.5.20是知乎日报api首次上线
                minDate.set(2017, 3, 9);
                minDate.set(2013, 4, 20);
                dialog.setMinDate(minDate);
                dialog.vibrate(false);
                dialog.show(getActivity().getFragmentManager(), "DatePickerDialog");
            }
        });


        showStep();
        lineChartStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImageShowActivity.startActivity(HealFragment.this,new ArrayList<HealthData>(list));
            }
        });


        ShowDistance();
        ShowCal();


    }

    private void showChooseData(String date) {
         List<String>  list_date  =new ArrayList<>();
        for (int i =  0 ;i<list.size();i++){
            list_date.add(list.get(i).getCreatDate());
        }
        int index = list_date.indexOf(date);

        if (index!= -1 ){

            Log.e("index",index+"");

            int Step = list.get(index).getStep();
            double Distance =list.get(index).getDistance();
            int cal = list.get(index).getCal();
            int sitHour = list.get(index).getSitHour();

            tvStep.setText(Step+"");
            tvDistance.setText(Distance+"");
            tvCal.setText(cal+"");
            tvSleepHour.setText(sitHour+"");
        }


    }



    private void ShowDistance(){
        int numSubcolumns = 1;//设置每个柱状图显示的颜色数量(每个柱状图显示多少块)

        int numColumns = 8;//柱状图的数量


        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        for (int i = 0; i < list.size(); ++i) {

            values = new ArrayList<SubcolumnValue>();

            SubcolumnValue value = new SubcolumnValue((float) list.get(i).getDistance(), ChartUtils.pickColor());//第一个值是数值(值>0 方向朝上，值<0，方向朝下)，第二个值是颜色
            //    SubcolumnValue value = new SubcolumnValue((float) Math.random() * 50f + 5, Color.parseColor("#00000000"));//第一个值是数值，第二个值是颜色
            //    values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
            values.add(value);


            Column column = new Column(values);//一个柱状图的实例
            column.setHasLabels(hasAxes);//设置是否显示每个柱状图的高度，
            column.setHasLabelsOnlyForSelected(false);//点击的时候是否显示柱状图的高度，和setHasLabels()和用的时候，setHasLabels()失效
            columns.add(column);
        }

         DistanceColumnChartData = new ColumnChartData(columns);//表格的数据实例
        if (hasAxes) {
            Axis axisX = new Axis();
            //   axisX.setInside(true);//是否显示在里面，默认为false
            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            for (int i =0 ; i <list.size();i ++){
                axisValues.add(new AxisValue(i).setLabel(date(list.get(i).getCreatDate())));

            }

//            AxisValue value1 = new AxisValue(0f);//值是在哪显示 0 是指 第0个柱状图
//            value1.setLabel(list.get());//设置显示的文本，默认为柱状图的位置
//            AxisValue value2 = new AxisValue(1.0f);
//            value2.setLabel(list.get(i));
//            AxisValue value3 = new AxisValue(2.0f);
//            value3.setLabel("[30-35]");
//            AxisValue value4 = new AxisValue(3.0f);
//            value4.setLabel("[35-45]");
//            AxisValue value5 = new AxisValue(4.0f);
//            value5.setLabel("[45-50]");
//            AxisValue value6 = new AxisValue(5.0f);
//            value6.setLabel("[50-55]");
//            AxisValue value7 = new AxisValue(6.0f);
//            value7.setLabel("[55-60]");
//
//            axisValues.add(value1);
//            axisValues.add(value2);
//            axisValues.add(value3);
//            axisValues.add(value4);
//            axisValues.add(value5);
//            axisValues.add(value6);
//            axisValues.add(value7);
//
            axisX.setValues(axisValues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {

                axisY.setName("公里");//设置Y轴的注释
            }
            DistanceColumnChartData.setAxisXBottom(axisX);//设置X轴显示的位置
            DistanceColumnChartData.setAxisYLeft(axisY);//设置Y轴显示的位置
        } else {
            DistanceColumnChartData.setAxisXBottom(null);
            DistanceColumnChartData.setAxisYLeft(null);
        }
        distanceColumnChart.setColumnChartData(DistanceColumnChartData);//为View设置数据



    }




    private void ShowCal(){
        int numSubcolumns = 1;//设置每个柱状图显示的颜色数量(每个柱状图显示多少块)

        int numColumns = 8;//柱状图的数量


        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;

        for (int i = 0; i < list.size(); ++i) {

            values = new ArrayList<SubcolumnValue>();

            SubcolumnValue value = new SubcolumnValue(list.get(i).getCal(), ChartUtils.pickColor());//第一个值是数值(值>0 方向朝上，值<0，方向朝下)，第二个值是颜色
            //    SubcolumnValue value = new SubcolumnValue((float) Math.random() * 50f + 5, Color.parseColor("#00000000"));//第一个值是数值，第二个值是颜色
            //    values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
            values.add(value);


            Column column = new Column(values);//一个柱状图的实例
            column.setHasLabels(hasAxes);//设置是否显示每个柱状图的高度，
            column.setHasLabelsOnlyForSelected(false);//点击的时候是否显示柱状图的高度，和setHasLabels()和用的时候，setHasLabels()失效
            columns.add(column);
        }

        CalColumnChartData = new ColumnChartData(columns);//表格的数据实例
        if (hasAxes) {
            Axis axisX = new Axis();
            //   axisX.setInside(true);//是否显示在里面，默认为false
            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            for (int i =0 ; i <list.size();i ++){
                axisValues.add(new AxisValue(i).setLabel(date(list.get(i).getCreatDate())));

            }

            axisX.setValues(axisValues);
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {

                axisY.setName("大卡");//设置Y轴的注释
            }
            CalColumnChartData.setAxisXBottom(axisX);//设置X轴显示的位置
            CalColumnChartData.setAxisYLeft(axisY);//设置Y轴显示的位置
        } else {
            CalColumnChartData.setAxisXBottom(null);
            CalColumnChartData.setAxisYLeft(null);
        }
        calColumnChart.setColumnChartData(CalColumnChartData);//为View设置数据

    }

    /**
     * 初始化LineChart的一些设置
     */
    private void showStep(){

         List<PointValue> Step_point = new ArrayList<PointValue>();
        //坐标点
         List<AxisValue>  Step_X = new ArrayList<AxisValue>();                 //Step表格_X轴

        /**
         * X 轴的显示
         */
        for (int i = 0; i < list.size(); i++) {
            Step_X.add(new AxisValue(i).setLabel(date(list.get(i).getCreatDate())));
        }
        /**
         * 图表的每个点的显示
         */

        for (int i = 0; i < list.size(); i++) {
            Step_point.add(new PointValue(i,list.get(i).getStep()));   //图表的每个点的显示
        }

        Line line = new Line(Step_point).setColor(Color.parseColor("#FFCD41"));  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(false);//曲线是否平滑
	    line.setStrokeWidth(1);//线条的粗细，默认是3
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
//		line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
//	    axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setTextColor(Color.parseColor("#D6D6D9"));//灰色

//	    axisX.setName("未来几天的天气");  //表格名称
        axisX.setTextSize(8);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
        axisX.setValues(Step_X);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//	    data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线


        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(8);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
        //设置行为属性，支持缩放、滑动以及平移
        lineChartStep.setInteractive(true);
        lineChartStep.setZoomType(ZoomType.HORIZONTAL);  //缩放类型，水平
        lineChartStep.setMaxZoom((float) 3);//缩放比例
        lineChartStep.setLineChartData(data);
        lineChartStep.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已,见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         * 下面几句可以设置X轴数据的显示个数（x轴0-7个数据），当数据点个数小于（29）的时候，缩小到极致hellochart默认的是所有显示。当数据点个数大于（29）的时候，
         * 若不设置axisX.setMaxLabelChars(int count)这句话,则会自动适配X轴所能显示的尽量合适的数据个数。
         * 若设置axisX.setMaxLabelChars(int count)这句话,
         * 33个数据点测试，若 axisX.setMaxLabelChars(10);里面的10大于v.right= 7; 里面的7，则
         刚开始X轴显示7条数据，然后缩放的时候X轴的个数会保证大于7小于10
         若小于v.right= 7;中的7,反正我感觉是这两句都好像失效了的样子 - -!
         * 并且Y轴是根据数据的大小自动设置Y轴上限
         * 若这儿不设置 v.right= 7; 这句话，则图表刚开始就会尽可能的显示所有数据，交互性太差
         */
        Viewport v = new Viewport(lineChartStep.getMaximumViewport());
        v.left = 0;
        v.right= 7;
        lineChartStep.setCurrentViewport(v);


    }

    private String date(String date){
       return Tools.formatTime(date,"yyyyMMdd","MM-dd");
    }
}
