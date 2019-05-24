//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.github.rinde.rinsim.core.model.DependencyProvider;
import com.github.rinde.rinsim.core.model.ModelBuilder.AbstractModelBuilder;
import com.github.rinde.rinsim.core.model.pdp.PDPModel;
import com.github.rinde.rinsim.core.model.pdp.PDPModel.VehicleState;
import com.github.rinde.rinsim.core.model.road.RoadModel;
import com.github.rinde.rinsim.core.model.road.RoadUser;
import com.github.rinde.rinsim.geom.Point;
import com.github.rinde.rinsim.ui.renderers.ViewPort;
import com.github.rinde.rinsim.ui.renderers.CanvasRenderer.AbstractCanvasRenderer;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.swt.graphics.GC;

public class TaxiRenderer extends AbstractCanvasRenderer {
    static final int ROUND_RECT_ARC_HEIGHT = 5;
    static final int X_OFFSET = -5;
    static final int Y_OFFSET = -30;
    final RoadModel roadModel;
    final PDPModel pdpModel;
    final TaxiRenderer.Language lang;

    TaxiRenderer(RoadModel r, PDPModel p, TaxiRenderer.Language l) {
        this.lang = l;
        this.roadModel = r;
        this.pdpModel = p;
    }

    public void renderStatic(GC gc, ViewPort vp) {
    }

    public void renderDynamic(GC gc, ViewPort vp, long time) {
        Map<RoadUser, Point> map = Maps.filterEntries(this.roadModel.getObjectsAndPositions(), TaxiRenderer.Pred.INSTANCE);
        Iterator var6 = map.entrySet().iterator();

        while(var6.hasNext()) {
            Entry<RoadUser, Point> entry = (Entry)var6.next();
            Taxi t = (Taxi)entry.getKey();
            Point p = (Point)entry.getValue();
            int x = vp.toCoordX(p.x) + -5;
            int y = vp.toCoordY(p.y) + -30;
            VehicleState vs = this.pdpModel.getVehicleState(t);
            String text = null;
            int size = (int)this.pdpModel.getContentsSize(t);
            if (vs == VehicleState.DELIVERING) {
                text = this.lang.disembark;
            } else if (vs == VehicleState.PICKING_UP) {
                text = this.lang.embark;
            } else if (size > 0) {
                text = Integer.toString(size);
            }

            if (text != null) {
                org.eclipse.swt.graphics.Point extent = gc.textExtent(text);
                gc.setBackground(gc.getDevice().getSystemColor(10));
                gc.fillRoundRectangle(x - extent.x / 2, y - extent.y / 2, extent.x + 2, extent.y + 2, 5, 5);
                gc.setForeground(gc.getDevice().getSystemColor(1));
                gc.drawText(text, x - extent.x / 2 + 1, y - extent.y / 2 + 1, true);
            }
        }

    }

    static TaxiRenderer.Builder builder(TaxiRenderer.Language l) {
        return new AutoValue_TaxiRenderer_Builder(l);
    }

    abstract static class Builder extends AbstractModelBuilder<TaxiRenderer, Void> {
        private static final long serialVersionUID = -1772420262312399129L;

        Builder() {
            this.setDependencies(new Class[]{RoadModel.class, PDPModel.class});
        }

        abstract TaxiRenderer.Language language();

        public TaxiRenderer build(DependencyProvider dependencyProvider) {
            RoadModel rm = (RoadModel)dependencyProvider.get(RoadModel.class);
            PDPModel pm = (PDPModel)dependencyProvider.get(PDPModel.class);
            return new TaxiRenderer(rm, pm, this.language());
        }
    }

    static enum Pred implements Predicate<Entry<RoadUser, Point>> {
        INSTANCE {
            public boolean apply(Entry<RoadUser, Point> input) {
                return input.getKey() instanceof Taxi;
            }
        };

        private Pred() {
        }
    }

    static enum Language {
        DUTCH("INSTAPPEN", "UITSTAPPEN"),
        ENGLISH("EMBARK", "DISEMBARK");

        final String embark;
        final String disembark;

        private Language(String s1, String s2) {
            this.embark = s1;
            this.disembark = s2;
        }
    }
}
