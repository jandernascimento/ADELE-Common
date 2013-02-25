import java.io.*
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

def artifactNameWithoutExtension = "target/resources-distrib-1.0-SNAPSHOT"

// check the zip distribution
def dist = new File (basedir, artifactNameWithoutExtension + ".zip");
assert dist.exists();
assert dist.canRead();

MessageDigest md = MessageDigest.getInstance("SHA1");

// get inputStream of felix jar
def zipFile = new ZipFile(dist);

assert zipFile.getEntry("toto.txt") != null;
assert zipFile.getEntry("titi.dscilia") != null;
assert zipFile.getEntry("conf").isDirectory();

for (ZipEntry e : zipFile.entries()){
    System.out.println("entry : " + e.getName());
}

