package com.daw.pms.Service.PMS.impl;

import com.daw.pms.DTO.Result;
import com.daw.pms.Service.PMS.LibraryService;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LibraryServiceImplTest {
  @Value("${pms.id}")
  private Long pmsId;

  @Autowired LibraryService libraryService;

  @Test
  void getLibraries() {
    try {
      Result pmsLibraries = libraryService.getLibraries(pmsId, null, null, null, null, 0);
      System.out.println("pmsLibraries: \n" + pmsLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicLibraries = libraryService.getLibraries(pmsId, null, null, null, null, 1);
      System.out.println("qqmusicLibraries: \n" + qqmusicLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmLibraries = libraryService.getLibraries(pmsId, null, null, null, null, 2);
      System.out.println("ncmLibraries: \n" + ncmLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result allCreatedFavLists = libraryService.getLibraries(pmsId, 1, 20, "web", 0, 3);
      System.out.println("allCreatedFavLists: \n" + allCreatedFavLists);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result allCollectedFavLists = libraryService.getLibraries(pmsId, 1, 20, "web", 0, 3);
      System.out.println("allCollectedFavLists: \n" + allCollectedFavLists);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void getDetailLibrary() {
    try {
      Result pmsLibrary =
          libraryService.getDetailLibrary("001", null, null, null, null, null, null, 0);
      System.out.println("pmsLibrary: \n" + pmsLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicLibrary =
          libraryService.getDetailLibrary("8729577481", null, null, null, null, null, null, 1);
      System.out.println("qqmusicLibrary: \n" + qqmusicLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmLibrary =
          libraryService.getDetailLibrary("8574846185", null, null, null, null, null, null, 2);
      System.out.println("ncmLibrary: \n" + ncmLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliLibrary =
          libraryService.getDetailLibrary("001", 1, 20, "keyword", "mtime", 0, 0, 3);
      System.out.println("bilibiliLibrary: \n" + bilibiliLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void createLibrary() {
    HashMap<String, String> library = new HashMap<>();
    library.put("name", "Test Library Name");
    try {
      Result result = libraryService.createLibrary(library, 0);
      System.out.println("pmsResult: \n" + result);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result result = libraryService.createLibrary(library, 1);
      System.out.println("qqmusicResult: \n" + result);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result result = libraryService.createLibrary(library, 2);
      System.out.println("ncmResult: \n" + result);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result result = libraryService.createLibrary(library, 3);
      System.out.println("bilibiliResult: \n" + result);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void deleteLibrary() {
    try {
      Result pmsDeletingResult = libraryService.deleteLibrary("1", 0);
      System.out.println("pmsDeletingResult: \n" + pmsDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicDeletingResult = libraryService.deleteLibrary("1", 1);
      System.out.println("qqmusicDeletingResult: \n" + qqmusicDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmDeletingResult = libraryService.deleteLibrary("1", 2);
      System.out.println("ncmDeletingResult: \n" + ncmDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliDeletingResult = libraryService.deleteLibrary("1", 3);
      System.out.println("bilibiliDeletingResult: \n" + bilibiliDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void addSongsToLibrary() {
    try {
      Result pmsAddingSongsResult =
          libraryService.addSongsToLibrary("1", null, "1,2", null, null, false, false, 0);
      System.out.println("pmsAddingSongsResult: \n" + pmsAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicAddingSongsResult =
          libraryService.addSongsToLibrary("1", null, "1,2", null, null, false, false, 1);
      System.out.println("qqmusicAddingSongsResult: \n" + qqmusicAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmAddingSongsResult =
          libraryService.addSongsToLibrary("1", null, "1,2", null, null, false, false, 2);
      System.out.println("ncmAddingSongsResult: \n" + ncmAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliAddingSongsResult =
          libraryService.addSongsToLibrary("1", "2", "1,2", null, null, false, false, 3);
      System.out.println("bilibiliAddingSongsResult: \n" + bilibiliAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void moveSongsToOtherLibrary() {
    try {
      Result pmsMovingSongsResult = libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 0);
      System.out.println("pmsMovingSongsResult: \n" + pmsMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicMovingSongsResult = libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 1);
      System.out.println("qqmusicMovingSongsResult: \n" + qqmusicMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmMovingSongsResult = libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 2);
      System.out.println("ncmMovingSongsResult: \n" + ncmMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliMovingSongsResult = libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 3);
      System.out.println("bilibiliMovingSongsResult: \n" + bilibiliMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void removeSongsFromLibrary() {
    try {
      Result pmsRemovingSongsResult = libraryService.removeSongsFromLibrary("1", "1,2", 0);
      System.out.println("pmsRemovingSongsResult: \n" + pmsRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result qqmusicRemovingSongsResult = libraryService.removeSongsFromLibrary("1", "1,2", 1);
      System.out.println("qqmusicRemovingSongsResult: \n" + qqmusicRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result ncmRemovingSongsResult = libraryService.removeSongsFromLibrary("1", "1,2", 2);
      System.out.println("ncmRemovingSongsResult: \n" + ncmRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Result bilibiliRemovingSongsResult = libraryService.removeSongsFromLibrary("1", "1,2", 3);
      System.out.println("bilibiliRemovingSongsResult: \n" + bilibiliRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
