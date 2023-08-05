package com.daw.pms.Entity.Bilibili;

import com.daw.pms.Entity.Basic.BasicLibrary;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Detail fav list of bilibili.
 *
 * @author Daw Loph
 * @version 1.0
 * @since 8/1/23
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class BiliDetailFavList extends BasicLibrary {
  /** The id of this fav list. */
  private Long id;

  /** The fid of the fav list. */
  private Long fid;

  /** The mid of the user. */
  private Long mid;

  /** The upper's name of this fav list. */
  private String upperName;

  /** The upper's head pic. */
  private String upperHeadPic;

  /** The view count of this fav list. */
  private Long viewCount;

  /** The collected count of this fav list. */
  private Long collectedCount;

  /** The like count of this fav list. */
  private Long likeCount;

  /** The share count of this fav list. */
  private Long shareCount;

  /** The danmaku count of this fav list. */
  private Long danmakuCount;

  /** The created time of this fav list. */
  private Long createdTime;

  /** The modified time of this fav list. */
  private Long modifiedTime;

  /** The introduction of this fav list. */
  private String intro;

  /** Whether there has more resources in this fav list. */
  private Boolean hasMore;

  /** The paged resources in this fav list. */
  private List<BiliResource> resources;

  /** The type of this fav list, 0 for created fav list, 1 for collected fav list. */
  private Integer type;
}
