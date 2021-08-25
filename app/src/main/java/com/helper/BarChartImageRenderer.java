package com.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;

public class BarChartImageRenderer extends BarChartRenderer {
    private final Bitmap[] imageToRender;

    public BarChartImageRenderer(BarDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler, Bitmap[] imageToRender) {
        super(chart, animator, viewPortHandler);
        this.imageToRender = imageToRender;
    }

    @Override
    protected void drawDataSet(Canvas c, IBarDataSet dataSet, int index) {
        super.drawDataSet(c, dataSet, index);
        drawBarImages(c, dataSet, index);

    }

    protected void drawBarImages(Canvas c, IBarDataSet dataSet, int index) {
        BarBuffer buffer = mBarBuffers[index];

        float left; //avoid allocation inside loop
        float right;
        float top;
        float bottom;


        for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {
            left = buffer.buffer[j];
            right = buffer.buffer[j + 2];
            top = buffer.buffer[j + 1];
            bottom = buffer.buffer[j + 3];

            float x = (left + right) / 2f;

            if (!mViewPortHandler.isInBoundsRight(x))
                break;

            if (!mViewPortHandler.isInBoundsY(top)
                    || !mViewPortHandler.isInBoundsLeft(x))
                continue;

            BarEntry entry = dataSet.getEntryForIndex(j / 4);
            float val = entry.getY();
            final Bitmap scaledBarImage = scaleBarImage(buffer, imageToRender[j / 4]);

            try {

                int starWidth = scaledBarImage.getWidth();
                int starOffset = starWidth / 2;

//                drawImage(c, scaledBarImage, x - starOffset, top);
                drawImage(c, scaledBarImage, x - starOffset, top - 60);
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
        }
    }

    private Bitmap scaleBarImage(BarBuffer buffer, Bitmap image) {
        try {
            float firstLeft = buffer.buffer[0];
            float firstRight = buffer.buffer[2];
            int firstWidth = (int) Math.ceil(firstRight - firstLeft);
            int finalSize = firstWidth * 2;
            return Bitmap.createScaledBitmap(image, finalSize, finalSize, false);
        } catch (Exception ex) {
            return null;
        }
    }

    protected void drawImage(Canvas c, Bitmap image, float x, float y) {
        if (image != null) {
            c.drawBitmap(image, x, y, null);
        }
    }

}