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
    private int mWidth, mHeight;
    private Point[] floorPath = new Point[4];
    private Point[] ceilPath = new Point[4];

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
        int floorBackLength = (int) (mWidth * 0.6f);
        int floorFrontLength = (int) (mWidth * 0.8f);
        floorPath[0] = new Point((int) (0.2f * mWidth), (int) (0.2f * mHeight));
        floorPath[1] = new Point((int) (0.8f * mWidth), (int) (0.2f * mHeight));
        floorPath[2] = new Point((int) (0.9f * mWidth), (int) (0.9f * mHeight));
        floorPath[3] = new Point((int) (0.1f * mWidth), (int) (0.9f * mHeight));

        ceilPath[0] = new Point((int) (0.18f * mWidth), (int) (0.1 * mHeight));
        ceilPath[1] = new Point((int) (0.82f * mWidth), (int) (0.1 * mHeight));
        ceilPath[2] = new Point((int) (0.95f * mWidth), (int) (0.8 * mHeight));
        ceilPath[3] = new Point((int) (0.05f * mWidth), (int) (0.8 * mHeight));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mWidth == 0) return;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(0xff555555);

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

        paint.setColor(0xffaaaaaa);

        Path pathBack = new Path();
        pathBack.moveTo(floorPath[0].x, floorPath[0].y);
        pathBack.lineTo(ceilPath[0].x, ceilPath[0].y);
        pathBack.lineTo(ceilPath[1].x, ceilPath[1].y);
        pathBack.lineTo(floorPath[1].x, floorPath[1].y);
        pathBack.close();
        canvas.drawPath(pathBack, paint);

        Path pathFront = new Path();
        pathFront.moveTo(floorPath[2].x, floorPath[2].y);
        pathFront.lineTo(ceilPath[2].x, ceilPath[2].y);
        pathFront.lineTo(ceilPath[3].x, ceilPath[3].y);
        pathFront.lineTo(floorPath[3].x, floorPath[3].y);
        pathFront.close();
        canvas.drawPath(pathFront, paint);

        int thickness = 20;
        Point[] periphery = new Point[4];
        periphery[0] = new Point(ceilPath[0].x - thickness, ceilPath[0].y - thickness);
        periphery[1] = new Point(ceilPath[1].x + thickness, ceilPath[1].y - thickness);
        periphery[2] = new Point(ceilPath[2].x + thickness, ceilPath[2].y + thickness);
        periphery[3] = new Point(ceilPath[3].x - thickness, ceilPath[3].y + thickness);
        Path sectionPath = new Path();
        sectionPath.moveTo(periphery[0].x, periphery[0].y);
        sectionPath.lineTo(periphery[1].x, periphery[1].y);
        sectionPath.lineTo(periphery[2].x, periphery[2].y);
        sectionPath.lineTo(periphery[3].x, periphery[3].y);
        sectionPath.close();


        paint.setColor(0xffbbbbbb);

        Path sectionPathInside = new Path();
        sectionPathInside.moveTo(ceilPath[0].x, ceilPath[0].y);
        sectionPathInside.lineTo(ceilPath[1].x, ceilPath[1].y);
        sectionPathInside.lineTo(ceilPath[2].x, ceilPath[2].y);
        sectionPathInside.lineTo(ceilPath[3].x, ceilPath[3].y);
        sectionPathInside.close();

        Path sectionPathInsideOld = new Path(sectionPathInside);

        Matrix matrix = new Matrix();
        matrix.postScale(1.05f, 1.05f, mWidth / 2, mHeight / 2);
        sectionPathInside.transform(matrix);
        sectionPathInside.op(sectionPathInsideOld, Path.Op.DIFFERENCE);

        canvas.drawPath(sectionPathInside, paint);

    }

    /**
     * 以中心点来缩放一个多边形
     *
     * @param quadrangle 多边形的顶点，
     * @return
     */
    private Point[] scaleQuadrangle(Point[] quadrangle, Point center, float scale) {
        class Line {
            //y = kx + b
            float k;
            float b;

            Line(float k, float b) {
                this.k = k;
                this.b = b;
            }
        }
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
            float k_line = (currPoint.y - nextPoint.y) / (float) (currPoint.x - nextPoint.x);
            float b_line = (currPoint.y - k_line * currPoint.x) * scale;

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

            float xIntersection = (nextLine.b - currLine.b) / (currLine.k - nextLine.k);
            float yIntersection = currLine.k * xIntersection + currLine.b;

            scaledQuadrangle[(i + 1) % scaledQuadrangle.length] = new Point((int) xIntersection, (int) yIntersection);
        }

        //再平移回去
        for (Point vertex : scaledQuadrangle) {
            vertex.x = vertex.x + center.x;
            vertex.y = vertex.y + center.y;
        }

        return scaledQuadrangle;
    }
}
