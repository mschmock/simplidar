import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.*;
import org.opengis.coverage.grid.*;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterValue;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.OverviewPolicy;
import org.geotools.gce.geotiff.GeoTiffReader;


public class DataLoaderTiff {
    public static void test(java.io.File file) throws Exception {

        ParameterValue<OverviewPolicy> policy = AbstractGridFormat.OVERVIEW_POLICY.createValue();
        policy.setValue(OverviewPolicy.IGNORE);

        //this will basically read 4 tiles worth of data at once from the disk...
        ParameterValue<String> gridsize = AbstractGridFormat.SUGGESTED_TILE_SIZE.createValue();

        //Setting read type: use JAI ImageRead (true) or ImageReaders read methods (false)
        ParameterValue<Boolean> useJaiRead = AbstractGridFormat.USE_JAI_IMAGEREAD.createValue();
        useJaiRead.setValue(true);


        GridCoverage2DReader reader = new GeoTiffReader(file);
        GridEnvelope dimensions = reader.getOriginalGridRange();
        GridCoordinates maxDimensions = dimensions.getHigh();
        int w = maxDimensions.getCoordinateValue(0)+1;
        int h = maxDimensions.getCoordinateValue(1)+1;
        int numBands = reader.getGridCoverageCount();

        GridCoverage2D coverage = reader.read(
            new GeneralParameterValue[]{policy, gridsize, useJaiRead}
        );
        GridGeometry2D geometry = coverage.getGridGeometry();


        for (int i=0; i<w; i++) {
            for (int j=0; j<h; j++) {

                org.geotools.geometry.Envelope2D pixelEnvelop =
                geometry.gridToWorld(new GridEnvelope2D(i, j, 1, 1));

                double lat = pixelEnvelop.getCenterY();
                double lon = pixelEnvelop.getCenterX();

                double[] vals = new double[numBands];
                coverage.evaluate(new GridCoordinates2D(i, j), vals);

                //Do something!

            }
        }

    }
}