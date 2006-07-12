// $Id$
package ucar.nc2.dt.trajectory;

import junit.framework.TestCase;
import ucar.nc2.dt.TrajectoryObsDataset;
import ucar.nc2.dt.TrajectoryObsDatatype;
import ucar.nc2.dt.PointObsDatatype;
import ucar.ma2.DataType;
import ucar.ma2.StructureData;
import ucar.ma2.InvalidRangeException;
import ucar.unidata.geoloc.LatLonRect;

import java.io.IOException;
import java.io.File;

/**
 * A description
 *
 * @author edavis
 * @since Feb 22, 2005T22:33:51 PM
 */
public class TestARMTrajectoryObsDataset extends TestCase
{
  private TrajectoryObsDataset me;

  private String testFilePath = TestTrajectoryObsDataset.getRemoteTestDataDir() + "/trajectory/sonde";
  private String testDataFileName = "sgpsondewnpnC1.a1.20020507.112400.cdf";

  public TestARMTrajectoryObsDataset( String name )
  {
    super( name );
  }

  protected void setUp()
  {
  }

  /**
   * Test ...
   */
  public void testARM() throws IOException {
    String location = testFilePath + "/" + testDataFileName;
    assertTrue( "Test file <" + location + "> does not exist.",
                new File( location ).exists() );
    try
    {
      me = TrajectoryObsDatasetFactory.open( location);
    }
    catch ( IOException e )
    {
      String tmpMsg = "Couldn't create TrajectoryObsDataset from ARM sonde file <" + location + ">: " + e.getMessage();
      assertTrue( tmpMsg,
                  false);
    }
    assertTrue( "Null TrajectoryObsDataset after open <" + location + "> ",
                me != null );
    assertTrue( "Dataset <" + location + "> not a ARMTrajectoryObsDataset.",
                me instanceof ARMTrajectoryObsDataset);

    String dsTitle = null;
    String dsDescrip = null;
    long dsStartDateLong = 1020770640000l;
    long dsEndDateLong = 1020776540000l;
    LatLonRect dsBoundBox = null;
    int dsNumGlobalAtts = 27;
    String exampleGlobalAttName = "history";
    String exampleGlobalAttVal = "created by the Zebra DataStore library";
    int dsNumVars = 10;
    String exampleVarName = "tdry";
    String exampleVarDescription = "Dry Bulb Temperature";
    String exampleVarUnitsString = "C";
    int exampleVarRank = 0;
    int[] exampleVarShape = new int[]{};
    String exampleVarDataType = DataType.FLOAT.toString();
    int exampleVarNumAtts = 4;
    Float exampleVarStartVal = new Float( 21.0f );
    Float exampleVarEndVal = new Float( -49.1f );
    int numTrajs = 1;
    String exampleTrajId = "trajectory data";
    String exampleTrajDesc = null;
    int exampleTrajNumPoints = 2951;
    float exampleTrajStartLat = 36.61f;
    float exampleTrajEndLat = 37.12618f;
    float exampleTrajStartLon = -97.49f;
    float exampleTrajEndLon = -96.28289f;
    float exampleTrajStartElev = 315.0f;
    float exampleTrajEndElev = 26771.0f;
    TestTrajectoryObsDataset.TrajDatasetInfo trajDsInfo =
            new TestTrajectoryObsDataset.TrajDatasetInfo(
                    dsTitle, dsDescrip, location,
                    dsStartDateLong, dsEndDateLong, dsBoundBox,
                    dsNumGlobalAtts, exampleGlobalAttName, exampleGlobalAttVal,
                    dsNumVars, exampleVarName, exampleVarDescription,
                    exampleVarUnitsString, exampleVarRank, exampleVarShape, exampleVarDataType, exampleVarNumAtts,
                    exampleVarStartVal, exampleVarEndVal,
                    numTrajs, exampleTrajId, exampleTrajDesc, exampleTrajNumPoints,
                    exampleTrajStartLat, exampleTrajEndLat, exampleTrajStartLon, exampleTrajEndLon, exampleTrajStartElev, exampleTrajEndElev);

    TestTrajectoryObsDataset.testTrajInfo( me, trajDsInfo );

    TrajectoryObsDatatype traj = me.getTrajectory( exampleTrajId);

    // Test that "alt" units gets changed to "meters"
    // ... from getPointObsData(0)
    PointObsDatatype pointOb;
    try
    {
      pointOb = (PointObsDatatype) traj.getPointObsData( 0 );
    }
    catch ( IOException e )
    {
      assertTrue( "IOException on call to getPointObsData(0): " + e.getMessage(),
                  false );
      return;
    }
    StructureData sdata;
    try
    {
      sdata = pointOb.getData();
    }
    catch ( IOException e )
    {
      assertTrue( "IOException on getData(): " + e.getMessage(),
                  false);
      return;
    }

    String u = sdata.findMember( "alt").getUnitsString();
    assertTrue( "traj.getPointObsData().getData().findMember( \"alt\") units <" + u + "> not as expected <meters>.",
                u.equals( "meters") );

    // ... from getData(0)
    try
    {
      sdata = traj.getData( 0 );
    }
    catch ( IOException e )
    {
      assertTrue( "IOException on getData(0): " + e.getMessage(),
                  false );
      return;
    }
    catch ( InvalidRangeException e )
    {
      assertTrue( "InvalidRangeException on getData(0): " + e.getMessage(),
                  false );
      return;
    }
    u = sdata.findMember( "alt" ).getUnitsString();
    assertTrue( "traj.getData().findMember( \"alt\") units <" + u + "> not as expected <meters>.",
                u.equals( "meters" ) );

  }

}

/*
 * $Log: TestARMTrajectoryObsDataset.java,v $
 * Revision 1.5  2006/06/06 16:07:17  caron
 * *** empty log message ***
 *
 * Revision 1.4  2006/05/08 02:47:37  caron
 * cleanup code for 1.5 compile
 * modest performance improvements
 * dapper reading, deal with coordinate axes as structure members
 * improve DL writing
 * TDS unit testing
 *
 * Revision 1.3  2005/05/25 20:53:42  edavis
 * Add some test data to CVS, the rest is on /upc/share/testdata.
 *
 * Revision 1.2  2005/05/23 22:47:01  edavis
 * Handle changing elevation data units (done in ncDataset
 * and record structure, needed some changes from John, too).
 *
 * Revision 1.1  2005/05/16 23:49:55  edavis
 * Add ucar.nc2.dt.trajectory.ARMTrajectoryObsDataset to handle
 * ARM sounding files. Plus a few other fixes and updates to the
 * tests.
 *
 * Revision 1.3  2005/05/11 00:10:10  caron
 * refactor StuctureData, dt.point
 *
 * Revision 1.2  2005/04/16 15:55:13  edavis
 * Fix Float10Trajectory. Improve testing.
 *
 * Revision 1.1  2005/03/18 00:29:08  edavis
 * Finish trajectory implementations with the new TrajectoryObsDatatype
 * and TrajectoryObsDataset interfaces and update tests.
 *
 * Revision 1.1  2005/03/01 22:02:24  edavis
 * Two more implementations of the TrajectoryDataset interface.
 *
 *
 */