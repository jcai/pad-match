package centipede.jni.test;

import android.test.AndroidTestCase;

import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import centipede.jni.DB;
import centipede.jni.Matcher;
import centipede.model.DataKey;
import centipede.services.MatcherOptions;
import centipede.services.MatcherService;
import centipede.util.HitQueue;

/**
 * Created by jcai on 5/10/16.
 */
public class MatcherServiceTest extends AndroidTestCase {
  private File mPath;
  private MatcherService matcherService;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    String packageDir = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).applicationInfo.dataDir;
    File dllPath = new File(packageDir + "/lib", "libsample.so");

    mPath = new File(getContext().getCacheDir(), "db-tests");
    MatcherOptions options = new MatcherOptions();
    options.dbPath = mPath;
    options.matchThreadNum = 2;
    options.dllPath = dllPath;
    matcherService = new MatcherService(options);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    matcherService.close();
    DB.destroy(mPath);
  }

  public void testMatch() throws Exception{
    byte[] tmpByte = new byte[]{'a','s'};
    DataKey key = new DataKey(100,"asdf");
    matcherService.put(key,tmpByte);
    matcherService.put(new DataKey(100,"fdsa"),tmpByte);

    HitQueue result = matcherService.match((byte) 100,key.get());

    Assert.assertEquals(2,result.size());
  }
  public void testMatchWithCachedBlock() throws Exception{
    byte[] tmpByte = new byte[]{'a','s'};
    DataKey key = new DataKey(100,"asdf");
    matcherService.put(key,tmpByte);
    HitQueue result = matcherService.match((byte) 100,key.get());
    Assert.assertEquals(1,result.size());

    result = matcherService.match((byte) 100,key.get());
    Assert.assertEquals(1,result.size());
  }
  public void testExtractAndPut() throws Exception{
    byte[] tmpByte = new byte[]{'a','s'};
    DataKey key = new DataKey(100,"asdf");
    matcherService.extractAndPut(key,tmpByte);
    byte[] featureData = matcherService.get(key);
    Assert.assertArrayEquals(featureData,tmpByte);
  }
}
