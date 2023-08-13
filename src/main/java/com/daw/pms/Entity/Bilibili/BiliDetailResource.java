package com.daw.pms.Entity.Bilibili;

import com.daw.pms.DTO.BiliLinksDTO;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The basic resource of bilibili, such as video, music, videos and official resources.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/1/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BiliDetailResource extends BiliResource {
  /** The aid of this resource, for favorite resources. */
  private Long aid;

  /** The cid of this resource. */
  private Long cid;

  /** Whether this resource has episodes. */
  private Boolean isSeasonResource;

  /** The upper's mid of this resource. */
  private Long upperMid;

  /** The upper's head picture of this resource. */
  private String upperHeadPic;

  /** The collected count of this resource. */
  private Long collectedCount;

  /** The comment count. */
  private Long commentCount;

  /** The coins of this resource. */
  private Long coinsCount;

  /** Shared count of this resource. */
  private Long sharedCount;

  /** The liked count of this resource. */
  private Long likedCount;

  /** The introduction of this resource. */
  private String intro;

  /** The published data of this resource. */
  private Long publishedTime;

  /** The created time of this resource. */
  private Long createdTime;

  /** The dynamic lables of this resource. */
  private String dynamicLabels;

  /** The subpages of this resource. */
  private List<BiliSubpageOfResource> subpages;

  /** The episodes of this resource. */
  private List<BiliDetailResource> episodes;

  /** The links of this resource. */
  private BiliLinksDTO links;
}
