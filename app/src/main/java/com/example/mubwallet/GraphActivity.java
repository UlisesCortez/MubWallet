package com.example.mubwallet;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mubwallet.Database.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GraphActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView tvTitle;
    private FrameLayout graphContainer;

    // TODO: reemplazar por el id real del usuario
    private final int idUser = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        dbHelper        = new DatabaseHelper(this);
        tvTitle         = findViewById(R.id.tvGraphTitle);
        graphContainer  = findViewById(R.id.graphContainer);

        String yearMonth = getIntent().getStringExtra("yearMonth"); // "YYYY-MM"
        if (yearMonth == null || yearMonth.length() < 7) {
            yearMonth = new SimpleDateFormat("yyyy-MM", Locale.US).format(new java.util.Date());
        }
        tvTitle.setText("Gasto mensual por suscripción • " + yearMonth);

        PieData data = loadPieData(yearMonth);
        if (data.sum <= 0f || data.values.isEmpty()) {
            Toast.makeText(this, "Sin datos para graficar en " + yearMonth, Toast.LENGTH_SHORT).show();
        }

        PieView view = new PieView(this);
        view.setData(data.labels, data.values, data.sum);
        graphContainer.removeAllViews();
        graphContainer.addView(view,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));
    }

    /** Carga totales por nombre de suscripción en el mes (YYYY-MM) */
    private PieData loadPieData(String yearMonth) {
        List<String> labels = new ArrayList<>();
        List<Float> values  = new ArrayList<>();
        float sum = 0f;

        Cursor c = null;
        try {
            c = dbHelper.getMonthlyTotalsByName(idUser, yearMonth);
            if (c != null) {
                int iName  = c.getColumnIndex("name");
                int iTotal = c.getColumnIndex("total");
                while (c.moveToNext()) {
                    String name  = c.getString(iName);
                    float total  = (float) c.getDouble(iTotal);
                    if (total <= 0f) continue;
                    labels.add(name == null || name.trim().isEmpty() ? "Sin nombre" : name.trim());
                    values.add(total);
                    sum += total;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) c.close();
        }
        if (sum <= 0f) sum = 1f; // evita división por 0 (se verá todo 0%)

        return new PieData(labels, values, sum);
    }

    // --------------------------- Data classes ---------------------------

    private static class PieData {
        final List<String> labels;
        final List<Float>  values;
        final float sum;
        PieData(List<String> labels, List<Float> values, float sum) {
            this.labels = labels;
            this.values = values;
            this.sum    = sum;
        }
    }

    // --------------------------- Custom View (Pie/Donut) ---------------------------

    /** Vista que dibuja un gráfico de pastel tipo dona con leyenda (sin librerías externas). */
    private static class PieView extends View {

        // Colores para reusar (puedes ajustar la paleta)
        private static final int[] COLORS = new int[]{
                0xFF0D7377, 0xFF3AAFA9, 0xFF17252A, 0xFF2B7A78,
                0xFF6B7280, 0xFFEF4444, 0xFF10B981, 0xFF3B82F6,
                0xFFF59E0B, 0xFF8B5CF6, 0xFFE11D48, 0xFF14B8A6
        };

        private final Paint slicePaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint holePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint textPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint legendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF oval        = new RectF();

        private List<String> labels = new ArrayList<>();
        private List<Float> values  = new ArrayList<>();
        private float sum = 1f;

        public PieView(Context ctx) {
            super(ctx);
            slicePaint.setStyle(Paint.Style.FILL);

            holePaint.setStyle(Paint.Style.FILL);
            holePaint.setColor(0xFFF4F7FC); // color de fondo (coincide con layout)

            textPaint.setColor(0xFF374151); // gris texto
            textPaint.setTextSize(26f);

            legendPaint.setStyle(Paint.Style.FILL);
        }

        public void setData(List<String> labels, List<Float> values, float sum) {
            this.labels = labels != null ? labels : new ArrayList<>();
            this.values = values != null ? values : new ArrayList<>();
            this.sum    = sum > 0f ? sum : 1f;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int w = getWidth();
            int h = getHeight();
            if (w == 0 || h == 0) return;

            // Zona de pie (izquierda) y leyenda (derecha)
            float padding = 24f;
            float legendWidth = Math.min(220f, w * 0.35f);

            float pieLeft   = padding;
            float pieTop    = padding;
            float pieRight  = w - legendWidth - padding;
            float pieBottom = h - padding;

            float size = Math.min(pieRight - pieLeft, pieBottom - pieTop);
            float cx = pieLeft + size / 2f;
            float cy = pieTop  + size / 2f;
            float radius = size / 2f;

            // Óvalo del pastel
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius);

            // Dibujar slices
            float startAngle = -90f;
            for (int i = 0; i < values.size(); i++) {
                float v = values.get(i);
                float sweep = (v / sum) * 360f;

                slicePaint.setColor(COLORS[i % COLORS.length]);
                canvas.drawArc(oval, startAngle, sweep, true, slicePaint);

                // Etiqueta de porcentaje (solo si el slice es suficientemente grande)
                if (sweep >= 12f) {
                    float midAngle = (float) Math.toRadians(startAngle + sweep / 2f);
                    float lr = radius * 0.62f;
                    float lx = (float) (cx + Math.cos(midAngle) * lr);
                    float ly = (float) (cy + Math.sin(midAngle) * lr);
                    String pct = String.format(Locale.US, "%.0f%%", (v / sum) * 100f);
                    float tw = textPaint.measureText(pct);
                    canvas.drawText(pct, lx - tw / 2f, ly + 8f, textPaint);
                }

                startAngle += sweep;
            }

            // Agujero (dona)
            canvas.drawCircle(cx, cy, radius * 0.45f, holePaint);

            // Leyenda
            float legendLeft = w - legendWidth + 12f;
            float y = padding + 6f;
            float box = 18f;
            float gap = 10f;
            float lineGap = 14f;

            for (int i = 0; i < labels.size(); i++) {
                legendPaint.setColor(COLORS[i % COLORS.length]);
                // cuadradito
                canvas.drawRect(legendLeft, y, legendLeft + box, y + box, legendPaint);

                // texto
                String lbl = labels.get(i);
                String pct = String.format(Locale.US, "  %s (%.0f%%)", lbl, (values.get(i) / sum) * 100f);
                canvas.drawText(pct, legendLeft + box + gap, y + box - 3f, textPaint);

                y += box + lineGap;
                if (y > h - padding) break; // no desbordar
            }
        }
    }
}
