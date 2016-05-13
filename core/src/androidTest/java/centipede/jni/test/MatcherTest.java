package centipede.jni.test;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import centipede.jni.CachedBlock;
import centipede.jni.DB;
import centipede.jni.Matcher;
import centipede.jni.PriorityQueue;


/**
 * Created by jcai on 5/8/16.
 */
public class MatcherTest extends AndroidTestCase{
  private File mPath;
  private DB mDb;
  private Matcher matcher;
  private String TAG="MatcherTest";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    mPath = new File(getContext().getCacheDir(), "db-tests-match");
    mDb = new DB(mPath);
    mDb.open();
    String packageDir = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).applicationInfo.dataDir;
    InputStream input = new FileInputStream(new File(packageDir+"/lib","libsample.so"));
    matcher = new Matcher(input,mDb);
    input.close();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    matcher.close();
    mDb.close();
    DB.destroy(mPath);
  }
  public void test_NotNull(){
    System.out.println("version .... "+Matcher.nativeGetDeviceId());
    Assert.assertNotNull(Matcher.nativeGetDeviceId());
  }
  public void test_matchOneToOne() throws Exception{
    byte[] tmpByte = new byte[1];
    Assert.assertEquals(100,matcher.matchOneToOne(tmpByte,tmpByte));
  }
  public void test_matchOneToMany(){
    byte[] tmpByte = new byte[]{'a'};
    mDb.put(tmpByte,tmpByte);
    byte[] endByte = new byte[]{'b'};
    PriorityQueue queue = matcher.matchOneToMany(tmpByte, tmpByte, endByte);
    Assert.assertEquals(1,queue.size());
    queue.close();
  }
  public void test_createMatchBlock(){
    byte[] tmpByte1 = new byte[]{'a'};
    mDb.put(tmpByte1,tmpByte1);
    byte[] tmpByte2 = new byte[]{'b'};
    mDb.put(tmpByte2,tmpByte2);
    byte[] tmpByte3 = new byte[]{'c'};
    mDb.put(tmpByte3,tmpByte3);
    CachedBlock block = matcher.createCachedBlock(tmpByte1,tmpByte3);
    Assert.assertEquals(2,block.Size());
    block.close();
  }
  public void test_matchOneToManyWithBlock(){
    byte[] tmpByte = new byte[]{'a'};
    mDb.put(tmpByte,tmpByte);
    byte[] endByte = new byte[]{'b'};
    mDb.put(tmpByte,tmpByte);
    CachedBlock block = matcher.createCachedBlock(tmpByte,endByte);
    Assert.assertEquals(1,block.Size());

    PriorityQueue queue = matcher.matchOneToMany(tmpByte,block);
    Assert.assertEquals(1,queue.size());

    queue.close();
    block.close();
  }
  public void test_extract(){
    byte[] tmpByte = new byte[]{'a','b','c'};
    byte[] result = matcher.extractFeature(tmpByte);
    org.junit.Assert.assertArrayEquals(tmpByte,result);
  }
}
