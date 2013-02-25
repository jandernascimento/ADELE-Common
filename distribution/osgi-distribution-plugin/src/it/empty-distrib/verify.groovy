import java.io.*;
import java.util.zip.ZipFile;

def artifactNameWithoutExtension = "target/empty-distrib-1.0-SNAPSHOT"

// check the zip distribution
def dist = new File (basedir, artifactNameWithoutExtension + ".zip");
assert dist.exists();
assert dist.canRead();

def zipFile = new ZipFile(dist);
assert zipFile.getEntry("bin").isDirectory();
assert zipFile.getEntry("felix.jar") != null;

