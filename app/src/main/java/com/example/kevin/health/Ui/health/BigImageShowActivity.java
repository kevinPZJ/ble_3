package com.example.kevin.health.Ui.health;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;


import com.example.kevin.health.Database.HealthData;
import com.example.kevin.health.R;
import com.example.kevin.health.base.BaseActivity;
import com.example.kevin.health.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class BigImageShowActivity extends BaseActivity {


    private List<PointValue> Step_point = new ArrayList<PointValue>();  //坐标点
    private List<AxisValue>  Step_X = new ArrayList<AxisValue>();   //Step_X坐标

    private List<HealthData> list = new ArrayList<>();

    private final static String LISTDATA = "list_data";
    private LineChartView lineChartStep;


    public static void startActivity(Fragment fragment, ArrayList<HealthData> DateList) {
        Intent intent = new Intent(fragment.getContext(), BigImageShowActivity.class);
        intent.putParcelableArrayListExtra(LISTDATA, DateList);
        fragment.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_show_activty);
        initView();
        list = getIntent().getParcelableArrayListExtra(LISTDATA);
        show();

    }

    private void show() {

        getAxisXLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化

    }

    private void initView() {
        lineChartStep = (LineChartView) findViewById(R.id.line_chart_step);
    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart(){
        Line line = new Line(Step_point).setColor(Color.parseColor("#FFCD41"));  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(false);//曲线是否平滑
        line.setStrokeWidth(3);//线条的粗细，默认是3
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
        axisX.setTextSize(11);//设置字体大小
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
        axisX.setValues(Step_X);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//	    data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线


        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(11);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
        //设置行为属性，支持缩放、滑动以及平移
        lineChartStep.setInteractive(true);
        lineChartStep.setZoomType(ZoomType.HORIZONTAL);  //缩放类型，水平
        lineChartStep.setMaxZoom((float) 6);//缩放比例
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


    /**
     * X 轴的显示
     */
    private void getAxisXLables(){
        for (int i = 0; i < list.size(); i++) {
            Step_X.add(new AxisValue(i).setLabel(date(list.get(i).getCreatDate())));
        }
    }
    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(){
        for (int i = 0; i < list.size(); i++) {
            Step_point.add(new PointValue(i,list.get(i).getStep()));
        }
    }
    private String date(String date){
        return Tools.formatTime(date,"yyyyMMdd","MM-dd");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
