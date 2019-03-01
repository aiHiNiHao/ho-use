package com.example.apple.viewpager2.house;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;


public class HouseView extends View {
    private final int SECTIONCOLOR = 0xffdddddd;//剖面
    private final int SIDECOLOR = 0xff555555;//侧面的深色阴影
    private final int FRONTCOLOR = 0xffaaaaaa;//正面的浅色阴影
    private final Point[] floorPath = new Point[4];
    private final Point floorIntersection = new Point();//这是所有纵向线条的远处的参考点，多有的纵向的线都相交于此地
    private final Point[] ceilPath = new Point[4];
    private final Point ceilIntersection = new Point();

    private int wallThickness ;//墙体厚度,反映到UI上就是，离参考点越近，值就越小
    private int mWidth, mHeight;

    public HouseView(Context context) {
        super(context);
    }

    public HouseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        init();
    }

    private void init() {
        wallThickness = 10;

        int floorBackLength = (int) (mWidth * 0.6f);
        int floorFrontLength = (int) (mWidth * 0.8f);
        floorPath[0] = new Point((int) (0.2f * mWidth), (int) (0.2f * mHeight));
        floorPath[1] = new Point((int) (0.8f * mWidth), (int) (0.2f * mHeight));
        floorPath[2] = new Point((int) (0.9f * mWidth), (int) (0.9f * mHeight));
        floorPath[3] = new Point((int) (0.1f * mWidth), (int) (0.9f * mHeight));

        Line floorLeftLine = Line.getLineByPoint(floorPath[3], floorPath[0]);
        Line floorRightLine = Line.getLineByPoint(floorPath[2], floorPath[1]);
        Point intersection1 = Line.getIntersection(floorLeftLine, floorRightLine);
        floorIntersection.x = intersection1.x;
        floorIntersection.y = intersection1.y;

        ceilPath[0] = new Point((int) (0.18f * mWidth), (int) (0.1 * mHeight));
        ceilPath[1] = new Point((int) (0.82f * mWidth), (int) (0.1 * mHeight));
        ceilPath[2] = new Point((int) (0.95f * mWidth), (int) (0.8 * mHeight));
        ceilPath[3] = new Point((int) (0.05f * mWidth), (int) (0.8 * mHeight));

        Line ceilLeftLine = Line.getLineByPoint(ceilPath[3], ceilPath[0]);
        Line ceilRightLine = Line.getLineByPoint(ceilPath[2], ceilPath[1]);
        Point intersection2 = Line.getIntersection(ceilLeftLine, ceilRightLine);
        ceilIntersection.x = intersection2.x;
        ceilIntersection.y = intersection2.y;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth == 0) return;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(SIDECOLOR);

        Path pathLeft = new Path();
        pathLeft.moveTo(floorPath[0].x, floorPath[0].y);
        pathLeft.lineTo(ceilPath[0].x, ceilPath[0].y);
        pathLeft.lineTo(ceilPath[3].x, ceilPath[3].y);
        pathLeft.lineTo(floorPath[3].x, floorPath[3].y);
        pathLeft.close();
        canvas.drawPath(pathLeft, paint);

        Path pathRight = new Path();
        pathRight.moveTo(floorPath[1].x, floorPath[1].y);
        pathRight.lineTo(ceilPath[1].x, ceilPath[1].y);
        pathRight.lineTo(ceilPath[2].x, ceilPath[2].y);
        pathRight.lineTo(floorPath[2].x, floorPath[2].y);
        pathRight.close();
        canvas.drawPath(pathRight, paint);

        paint.setColor(FRONTCOLOR);

        Path pathBack = new Path();
        pathBack.moveTo(floorPath[0].x, floorPath[0].y);
        pathBack.lineTo(ceilPath[0].x, ceilPath[0].y);
        pathBack.lineTo(ceilPath[1].x, ceilPath[1].y);
        pathBack.lineTo(floorPath[1].x, floorPath[1].y);
        pathBack.close();
        canvas.drawPath(pathBack, paint);

        Point floorCenter = new Point((floorPath[0].x + floorPath[2].x)/2, (floorPath[0].y + floorPath[2].y)/2);
        Point[] scaledFloorPoints = scaleQuadrangle(floorPath, floorCenter);
        Point ceilCenter = new Point((ceilPath[0].x + ceilPath[2].x)/2, (ceilPath[0].y + ceilPath[2].y)/2);
        Point[] scaledCeilPoints = scaleQuadrangle(ceilPath, ceilCenter);

        Path sectionPath = new Path();
        sectionPath.moveTo(scaledCeilPoints[0].x, scaledCeilPoints[0].y);
        sectionPath.lineTo(scaledCeilPoints[1].x, scaledCeilPoints[1].y);
        sectionPath.lineTo(scaledCeilPoints[2].x, scaledCeilPoints[2].y);
        sectionPath.lineTo(scaledCeilPoints[3].x, scaledCeilPoints[3].y);
        sectionPath.close();

        paint.setColor(SECTIONCOLOR);

        Path sectionPathInside = new Path();
        sectionPathInside.moveTo(ceilPath[0].x, ceilPath[0].y);
        sectionPathInside.lineTo(ceilPath[1].x, ceilPath[1].y);
        sectionPathInside.lineTo(ceilPath[2].x, ceilPath[2].y);
        sectionPathInside.lineTo(ceilPath[3].x, ceilPath[3].y);
        sectionPathInside.close();

        sectionPath.op(sectionPathInside, Path.Op.DIFFERENCE);

        canvas.drawPath(sectionPath, paint);

        paint.setColor(FRONTCOLOR);

        Path pathFront = new Path();
        pathFront.moveTo(scaledFloorPoints[2].x, scaledFloorPoints[2].y);
        pathFront.lineTo(scaledCeilPoints[2].x, scaledCeilPoints[2].y);
        pathFront.lineTo(scaledCeilPoints[3].x, scaledCeilPoints[3].y);
        pathFront.lineTo(scaledFloorPoints[3].x, scaledFloorPoints[3].y);
        pathFront.close();
        canvas.drawPath(pathFront, paint);

    }

    /**
     * 得到围墙的外围顶点
     *
     * @param quadrangle 围墙的内部顶点，
     * @return
     */
    private Point[] scaleQuadrangle(Point[] quadrangle, Point center) {

        Point[] scaledQuadrangle = new Point[quadrangle.length];
        Line[] lines = new Line[quadrangle.length];
        //把中心点当做坐标系的原点(平移)
        for (Point vertex : quadrangle) {
            vertex.x = vertex.x - center.x;
            vertex.y = vertex.y - center.y;
        }

        for (int i = 0; i < quadrangle.length; i++) {
            Point currPoint = quadrangle[i];
            Point nextPoint;
            if (i < quadrangle.length - 1) {
                nextPoint = quadrangle[i + 1];
            } else {
                nextPoint = quadrangle[0];
            }

            // TODO: 3/1/19
            float k_line = (currPoint.y - nextPoint.y) / (float) (currPoint.x - nextPoint.x);
            float b_line = currPoint.y - k_line * currPoint.x;

            lines[i] = new Line(k_line, b_line);

        }

        for (int i = 0; i < lines.length; i++) {
            Line currLine = lines[i];
            Line nextLine;
            if (i < quadrangle.length - 1) {
                nextLine = lines[i + 1];
            } else {
                nextLine = lines[0];
            }


            scaledQuadrangle[(i + 1) % scaledQuadrangle.length] = Line.getIntersection(currLine, nextLine);
        }

        //再平移回去
        for (Point vertex : scaledQuadrangle) {
            vertex.x = vertex.x + center.x;
            vertex.y = vertex.y + center.y;
        }
        for (Point vertex : quadrangle) {
            vertex.x = vertex.x + center.x;
            vertex.y = vertex.y + center.y;
        }

        return scaledQuadrangle;
    }

    static class Line {
        //y = kx + b
        float k;
        float b;

        Line(float k, float b) {
            this.k = k;
            this.b = b;
        }

        /**
         * 两点确定一条直线
         * @param p1
         * @param p2
         * @return
         */
        static Line getLineByPoint(Point p1, Point p2){
            float k_line = (p1.y - p2.y) / (float) (p1.x - p2.x);
            float b_line = p1.y - k_line * p1.x;

            return new Line(k_line, b_line);
        }


        /**
         * 获取两条直线的交点
         * @param l1
         * @param l2
         * @return
         */
        static Point getIntersection(Line l1, Line l2){

            float xIntersection = (l2.b - l1.b) / (l1.k - l2.k);
            float yIntersection = l1.k * xIntersection + l1.b;

            return new Point((int) xIntersection, (int) yIntersection);
        }
    }
}
