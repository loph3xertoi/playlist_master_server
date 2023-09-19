package com.daw.pms.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiniu.cdn.CdnManager;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Util for Qiniu Oss.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@Component
public class QiniuOSS {
  @Value("${qiniu.access-key}")
  private String accessKey;

  @Value("${qiniu.secret-key}")
  private String secretKey;

  @Value("${qiniu.bucket}")
  private String bucket;

  @Value("${qiniu.domain}")
  private String domain;

  /**
   * Upload file.
   *
   * @param fileInputStream File input stream.
   * @param fileName The file name.
   * @return The final key.
   * @throws com.qiniu.common.QiniuException com.qiniu.common.QiniuException.
   * @throws com.fasterxml.jackson.core.JsonProcessingException
   *     com.fasterxml.jackson.core.JsonProcessingException.
   * @throws java.io.UnsupportedEncodingException java.io.UnsupportedEncodingException.
   */
  public String uploadFileByInputStream(InputStream fileInputStream, String fileName)
      throws QiniuException, JsonProcessingException, UnsupportedEncodingException {
    Configuration cfg = new Configuration(Region.huanan());
    cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;
    UploadManager uploadManager = new UploadManager(cfg);

    Auth auth = Auth.create(accessKey, secretKey);
    String key;
    if (fileName == null || fileName.equals("")) {
      key = null;
    } else {
      key = fileName;
    }
    String upToken = auth.uploadToken(bucket, key);
    Response response = uploadManager.put(fileInputStream, key, upToken, null, null);
    DefaultPutRet putRet = new ObjectMapper().readValue(response.bodyString(), DefaultPutRet.class);
    String finalKey = putRet.key;
    String finalKeyWithExtend = finalKey + ".jpg";
    BucketManager bucketManager = new BucketManager(auth, cfg);
    bucketManager.move(bucket, finalKey, bucket, finalKeyWithExtend, true);
    String encodedFileName = URLEncoder.encode(finalKey, "utf-8").replace("+", "%20") + ".jpg";
    String publicUrl = String.format("%s/%s", domain, encodedFileName);
    CdnManager c = new CdnManager(auth);
    String[] urls = new String[] {publicUrl};
    c.refreshUrls(urls);
    return finalKey;
  }

  /**
   * Reflect key to final Oss link.
   *
   * @param key The original key.
   * @return Final Oss link.
   * @throws java.io.UnsupportedEncodingException java.io.UnsupportedEncodingException.
   * @throws com.qiniu.common.QiniuException com.qiniu.common.QiniuException.
   */
  public String reflectKeyToFinalOSSLink(String key)
      throws UnsupportedEncodingException, QiniuException {
    String encodedFileName = URLEncoder.encode(key, "utf-8").replace("+", "%20") + ".jpg";
    String publicUrl = String.format("%s/%s", domain, encodedFileName);
    Auth auth = Auth.create(accessKey, secretKey);
    long expireInSeconds = 1200;
    return auth.privateDownloadUrl(publicUrl, expireInSeconds);
  }
}
