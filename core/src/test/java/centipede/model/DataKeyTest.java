package centipede.model;

import junit.framework.Assert;

import org.junit.Test;

import centipede.util.BytesRef;

/**
 * Created by jcai on 5/9/16.
 */
public class DataKeyTest {
  @Test
  public void testDataKey(){
    DataKey key = new DataKey(1,"as中文df");
    Assert.assertEquals(1,(byte)key.getDataType());
    Assert.assertEquals("as中文df",key.getKey().utf8ToString());
    Assert.assertEquals(62099,key.getBlockIdNum());
    DataKey key2 = new DataKey(2,"as中文测试df");
    Assert.assertEquals(2,(byte)key2.getDataType());
    Assert.assertEquals("as中文测试df",key2.getKey().utf8ToString());
    Assert.assertEquals(44965,key2.getBlockIdNum());


    //验证blockId的一致性
    int blockNum = key2.getBlockIdNum();
    BytesRef bytesRef = DataKey.createDataKeyByBlockNum((byte)0,blockNum);
    Assert.assertEquals(bytesRef.bytes[1],key2.toByteArray()[1]);
    Assert.assertEquals(bytesRef.bytes[2],key2.toByteArray()[2]);

  }
}
