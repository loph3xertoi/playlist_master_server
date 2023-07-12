import com.daw.pms.Entity.QQMusic.QQMusicUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class UnitTest {

  @Test
  public void whenUsingJsonNode_thenOk() throws IOException {
    String SOURCE_JSON =
        "{\"code\":0,\"data\":{\"creator\":{\"nick\":\"daw\",\"headpic\":\"http://thirdqq.qlogo.cn/g?b=sdk&k=D86UImnAtLoOf1tpib7xeaQ&s=410&t=1556823363\"},\"lvinfo\":[{\"iconurl\":\"http://y.gtimg.cn/music/icon/v1/h5/svip_g.png\"}],\"listeninfo\":{\"iconurl\":\"http://y.gtimg.cn/music/icon/h5/listen/lv6.png\"},\"backpic\":{\"picurl\":\"http://y.gtimg.cn/music/photo_new/T009R750x1334M000004JDRgV2XBQH9.jpg\"},\"nums\":{\"visitornum\":103,\"fansnum\":2,\"follownum\":11,\"followusernum\":6,\"followsingernum\":5,\"frdnum\":71}}}";
    JsonNode productNode = new ObjectMapper().readTree(SOURCE_JSON);
    QQMusicUser qqMusicHomepage = new QQMusicUser();
    qqMusicHomepage.setName(productNode.get("data").get("creator").get("nick").textValue());
    System.out.println(qqMusicHomepage);
    //    qqMusicHomepage.setId(productNode.get("id").textValue());
    //    qqMusicHomepage.setName(productNode.get("name").textValue());
    //    qqMusicHomepage.setBrandName(productNode.get("brand")
    //      .get("name").textValue());
    //    qqMusicHomepage.setOwnerName(productNode.get("brand")
    //      .get("owner").get("name").textValue());
    //
    //    assertEquals(product.getName(), "The Best Product");
    //    assertEquals(product.getBrandName(), "ACME Products");
    //    assertEquals(product.getOwnerName(), "Ultimate Corp, Inc.");
  }
}
