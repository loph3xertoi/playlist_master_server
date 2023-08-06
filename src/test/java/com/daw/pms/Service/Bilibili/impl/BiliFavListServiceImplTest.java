package com.daw.pms.Service.Bilibili.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.Bilibili.BiliFavListService;
import java.util.HashMap;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BiliFavListServiceImplTest {
  @Value("${bilibili.id}")
  private Long biliId;

  @Value("${bilibili.cookie}")
  private String biliCookie;

  private static Long favListId;

  @Autowired BiliFavListService biliFavListService;

  @Test
  void getFavLists() {
    Result allCreatedFavLists = biliFavListService.getFavLists(1, 20, biliId, "web", 0, biliCookie);
    System.out.println("All created fav lists: \n" + allCreatedFavLists);
    Result allCollectedFavLists =
        biliFavListService.getFavLists(1, 20, biliId, "web", 1, biliCookie);
    System.out.println("All collected fav lists: \n" + allCollectedFavLists);
  }

  @Test
  void getDetailFavList() {
    Result detailCreatedFavList =
        biliFavListService.getDetailFavList(172408678L, 1, 20, null, "mtime", 0, 0, biliCookie);
    System.out.println("Detail created fav list: \n" + detailCreatedFavList);
    Result detailCollectedFavList =
        biliFavListService.getDetailFavList(124330L, 1, 20, null, "mtime", 0, 1, biliCookie);
    System.out.println("Detail collected fav list: \n" + detailCollectedFavList);
  }

  @Test
  @Order(1)
  void createFavList() {
    HashMap<String, String> favList = new HashMap<>();
    favList.put("title", "test fav list");
    favList.put("intro", "intro");
    favList.put("privacy", "0");
    Result result = biliFavListService.createFavList(favList, biliCookie);
    favListId = Long.valueOf(result.getData().toString());
    System.out.println(result);
  }

  @Test
  @Order(3)
  void deleteFavList() {
    Result result = biliFavListService.deleteFavList(favListId.toString(), biliCookie);
    System.out.println(result);
  }

  @Test
  @Order(2)
  void editFavList() {
    Result result =
        biliFavListService.editFavList(favListId, "new title", "new intro", 0, "", biliCookie);
    System.out.println(result);
  }

  @Test
  void multipleAddResources() {
    Result result =
        biliFavListService.multipleAddResources(
            "172408678", "2407308978", biliId, "910235969:2,381421301:2", "web", biliCookie);
    System.out.println(result);
  }

  @Test
  void multipleMoveResources() {
    Result result =
        biliFavListService.multipleMoveResources(
            2407308978L, 2478777678L, biliId, "910235969:2,381421301:2", "web", biliCookie);
    System.out.println(result);
  }

  @Test
  void multipleDeleteResources() {
    Result result =
        biliFavListService.multipleDeleteResources(
            "910235969:2,381421301:2", 2478777678L, "web", biliCookie);
    System.out.println(result);
  }

  @Test
  void favoriteResourceToFavLists() {
    Result result =
        biliFavListService.favoriteResourceToFavLists(231844300L, 2, "2422499978", biliCookie);
    System.out.println(result);
  }

  @Test
  void getCsrf() {
    String csrf = biliFavListService.getCsrf(biliCookie);
    System.out.println(csrf);
  }
}
