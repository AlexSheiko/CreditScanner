/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.creditscanner.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import com.creditscanner.android.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.Locale;

/**
 * A very simple Processor which gets detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        // Process all recognized items
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);

            // Draw a label
            if (item.getValue() != null && isNumber(item) && item.getValue().length() > 2) {
                OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
                mGraphicOverlay.add(graphic);
            }

            // Dial the code if needed
            if (item.getValue() != null && isCouponCode(item)) {
                String code = item.getValue().replaceAll("\\D+", "");
                Context context = mGraphicOverlay.getContext();
                int operator = PreferenceManager.getDefaultSharedPreferences(context).getInt("operator", 0);
                if (operator != 0) {
                    dial(operator, code);
                } else {
                    Intent intent = new Intent(context, OperatorInputActivity.class);
                    intent.putExtra("code", code);
                    context.startActivity(intent);
                }
                // Prevent dial screen from opening multiple times
                return;
            }
        }
    }

    @Override
    public void release() {
        mGraphicOverlay.clear();
    }

    private boolean isNumber(TextBlock text) {
        return text.getValue().matches("[0-9]+");
    }

    private boolean isCouponCode(TextBlock text) {
        String numbers = text.getValue().replaceAll("\\D+", "");
        return numbers.length() >= 14 & numbers.length() <= 16;
    }

    private void dial(int operatorCode, String voucherCode) {
        String number = String.format(Locale.getDefault(), "*%d*%s", operatorCode, voucherCode);
        Context context = mGraphicOverlay.getContext();

        Intent call = new Intent(Intent.ACTION_DIAL);
        call.setData(Uri.parse("tel:" + number + Uri.encode("#")));
        context.startActivity(call);
    }
}
