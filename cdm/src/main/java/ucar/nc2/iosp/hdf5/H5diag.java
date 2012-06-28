package ucar.nc2.iosp.hdf5;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.io.IOException;
import java.util.Formatter;

/**
 * HDF5 diagnostic helper
 *
 * @author caron
 * @since 6/25/12
 */
public class H5diag {
  private H5iosp iosp;

  public H5diag(H5iosp iosp) {
    this.iosp = iosp;
  }

  private class Size {
    long storage;
    long nominal;
    int count;

    private Size(long storage, int count) {
      this.storage = storage;
      this.count = count;
    }

    float getRatio() {
      if (storage == 0) return 0;
      return ((float) nominal / storage) ;
    }
  }

  public void showCompress(Formatter f) throws IOException {
    NetcdfFile ncfile = iosp.getNetcdfFile();

    Size totalSize = new Size(0,0);
    for (Variable v : ncfile.getVariables())  {
      H5header.Vinfo vinfo = (H5header.Vinfo) v.getSPobject();
      showCompress(v, vinfo, totalSize, f);
    }
    f.format("%n");
    f.format(" total bytes   = %d%n", totalSize.nominal);
    f.format(" total storage = %d%n", totalSize.storage);
    f.format(" compression = %f%n", totalSize.getRatio());
    f.format(" nchunks     = %d%n", totalSize.count);

    File raf = new File(ncfile.getLocation());
    f.format(" file size    = %d%n", raf.length());

    f.format(" overhead     = %f%n", ((float) raf.length()/totalSize.storage));
  }

  private void showCompress(Variable v, H5header.Vinfo vinfo, Size total, Formatter f) throws IOException {
    H5header.MessageDataspace mdt = vinfo.mds;

    long total_elems = 1;
    f.format("%8s %-40s(", v.getDataType(), v.getShortName());
    for (int len : mdt.dimLength) {
      f.format("%d ", len);
      total_elems *= len;
    }
    boolean sizeOk = total_elems == v.getSize();
    total_elems = v.getSize();

    long nominalSize = total_elems * v.getElementSize();

    Size size = new Size(nominalSize, 1);
    countStorageSize(vinfo, size);
    total.storage += size.storage;
    total.nominal += nominalSize;
    total.count += size.count;
    float ratio =  (size.storage == 0) ? 0 : (float) nominalSize / size.storage;

    f.format(") == %d nelems %s == %d bytes storage = %d (%f) nchunks = %d%n", total_elems, sizeOk ? "" : "*", nominalSize, size.storage, ratio, size.count);
  }

  private void countStorageSize(H5header.Vinfo vinfo, Size size) throws IOException {
    DataBTree btree = vinfo.btree;
    if (btree == null || vinfo.useFillValue) {
      size.storage = 0;
      size.count = 0;
      return; // 0 storage
    }

    int count = 0;
    long total = 0;
    DataBTree.DataChunkIterator iter = btree.getDataChunkIteratorFilter(null);
    while (iter.hasNext()) {
      DataBTree.DataChunk dc = iter.next();
      total += dc.size;
      count++;
    }

    size.storage = total;
    size.count = count;
  }

  //////////////////////////////////////////////////////////////////////////////////////////////

}
