package com.daw.pms.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.StringWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling mpd file.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 9/18/23
 */
@RestController
@RequestMapping("/mpd")
public class MpdController {
  private final RedisTemplate<String, String> redisTemplate;

  /**
   * Constructor for MpdController.
   *
   * @param redisTemplate a {@link org.springframework.data.redis.core.RedisTemplate} object.
   */
  public MpdController(RedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * Get mpd file.
   *
   * @param fileName The mpd file name.
   * @return The mpd file.
   */
  @Operation(summary = "Get mpd file.")
  @ApiResponse(description = "The mpd file.")
  @GetMapping(value = "/{fileName:.+\\.mpd}")
  public ResponseEntity<byte[]> getMpdFile(
      @Parameter(description = "The mpd file name.") @PathVariable String fileName) {
    String mpdXml = redisTemplate.opsForValue().get("bili-mpd::" + fileName);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.parseMediaType("application/dash+xml"));
    assert mpdXml != null;
    return ResponseEntity.status(HttpStatus.OK).headers(headers).body(mpdXml.getBytes());
  }

  //  @GetMapping(value = "/xml")
  //  public ResponseEntity<String> testXML() throws XMLStreamException {
  //    HttpHeaders headers = new HttpHeaders();
  //    //    headers.setContentType(MediaType.parseMediaType("application/dash+xml"));
  //    headers.setContentType(MediaType.APPLICATION_XML);
  //    headers.setAccessControlAllowOrigin("*");
  //    headers.setAccessControlAllowHeaders(
  //        new ArrayList<String>() {
  //          {
  //            add("*");
  //          }
  //        });
  //    return ResponseEntity.status(HttpStatus.OK).headers(headers).body(generateMPD(null));
  //  }

  private String generateMPD() throws XMLStreamException {
    XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    StringWriter stringWriter = new StringWriter();
    XMLStreamWriter xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(stringWriter);

    // Start the document
    xmlStreamWriter.writeStartDocument();
    xmlStreamWriter.writeStartElement("MPD");
    xmlStreamWriter.writeAttribute("xmlns", "urn:mpeg:dash:schema:mpd:2011");
    xmlStreamWriter.writeAttribute("type", "static");
    xmlStreamWriter.writeAttribute("mediaPresentationDuration", "PT0H3M58.00S");
    xmlStreamWriter.writeAttribute("minBufferTime", "PT1.5S");
    xmlStreamWriter.writeAttribute(
        "profiles", "urn:mpeg:dash:profile:isoff-live:2011,urn:com:dashif:dash264");

    xmlStreamWriter.writeStartElement("Period");
    xmlStreamWriter.writeAttribute("id", "1");
    xmlStreamWriter.writeAttribute("start", "PT0S");

    // Write audio adaptation set.
    xmlStreamWriter.writeStartElement("AdaptationSet");
    xmlStreamWriter.writeAttribute("group", "1");
    xmlStreamWriter.writeAttribute("mimeType", "audio/mp4");
    xmlStreamWriter.writeAttribute("minBandwidth", "319111");
    xmlStreamWriter.writeAttribute("maxBandwidth", "319111");
    xmlStreamWriter.writeAttribute("segmentAlignment", "true");
    xmlStreamWriter.writeAttribute("startWithSAP", "0");
    xmlStreamWriter.writeStartElement("SegmentBase");
    xmlStreamWriter.writeAttribute("indexRange", "934-1541");
    xmlStreamWriter.writeStartElement("Initialization");
    xmlStreamWriter.writeAttribute("range", "0-933");
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeStartElement("Representation");
    xmlStreamWriter.writeAttribute("id", "30280");
    xmlStreamWriter.writeAttribute("bandwidth", "319111");
    xmlStreamWriter.writeAttribute("width", "0");
    xmlStreamWriter.writeAttribute("height", "0");
    xmlStreamWriter.writeStartElement("BaseURL");
    xmlStreamWriter.writeCharacters(
        "https://upos-sz-mirror08c.bilivideo.com/upgcxcode/32/30/893463032/893463032-1-30280.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfq9rVEuxTEnE8L5F6VnEsSTx0vkX8fqJeYTj_lta53NCM=&amp;uipk=5&amp;nbs=1&amp;deadline=1691767863&amp;gen=playurlv2&amp;os=08cbv&amp;oi=1879688016&amp;trid=288bab1d8e8c4459a246302da104b4acT&amp;mid=319771778&amp;platform=html5&amp;upsig=6e46f19a8d6709615d12b4dfc2179ed9&amp;uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&amp;bvc=vod&amp;nettype=0&amp;bw=39894&amp;orderid=0,1&amp;buvid=&amp;build=0&amp;mobi_app=&amp;logo=80000000");
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();

    // Write video adaptation set.
    xmlStreamWriter.writeStartElement("AdaptationSet");
    xmlStreamWriter.writeAttribute("group", "4");
    xmlStreamWriter.writeAttribute("mimeType", "video/mp4");
    xmlStreamWriter.writeAttribute("minBandwidth", "2718771");
    xmlStreamWriter.writeAttribute("maxBandwidth", "2718771");
    xmlStreamWriter.writeAttribute("segmentAlignment", "true");
    xmlStreamWriter.writeAttribute("startWithSAP", "1");
    xmlStreamWriter.writeStartElement("SegmentBase");
    xmlStreamWriter.writeAttribute("indexRange", "995-1590");
    xmlStreamWriter.writeStartElement("Initialization");
    xmlStreamWriter.writeAttribute("range", "0-994");
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeStartElement("Representation");
    xmlStreamWriter.writeAttribute("id", "80");
    xmlStreamWriter.writeAttribute("sar", "1:1");
    xmlStreamWriter.writeAttribute("bandwidth", "2718771");
    xmlStreamWriter.writeAttribute("width", "1920");
    xmlStreamWriter.writeAttribute("height", "1080");
    xmlStreamWriter.writeAttribute("frameRate", "30");
    xmlStreamWriter.writeAttribute("codecs", "avc1.640032");
    xmlStreamWriter.writeStartElement("BaseURL");
    xmlStreamWriter.writeCharacters(
        "https://upos-sz-mirrorali.bilivideo.com/upgcxcode/32/30/893463032/893463032-1-30080.m4s?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfq9rVEuxTEnE8L5F6VnEsSTx0vkX8fqJeYTj_lta53NCM=&amp;uipk=5&amp;nbs=1&amp;deadline=1691767861&amp;gen=playurlv2&amp;os=alibv&amp;oi=1879688016&amp;trid=288bab1d8e8c4459a246302da104b4acT&amp;mid=319771778&amp;platform=html5&amp;upsig=b1f288a29cf1011e6a3d9bca5438fe61&amp;uparams=e,uipk,nbs,deadline,gen,os,oi,trid,mid,platform&amp;bvc=vod&amp;nettype=0&amp;bw=341243&amp;orderid=0,1&amp;buvid=&amp;build=0&amp;mobi_app=&amp;logo=80000000");
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();

    // End the document
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndDocument();
    xmlStreamWriter.flush();
    xmlStreamWriter.close();

    return stringWriter.toString();
  }
}
