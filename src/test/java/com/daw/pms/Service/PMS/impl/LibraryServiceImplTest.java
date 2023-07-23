package com.daw.pms.Service.PMS.impl;

import com.daw.pms.Entity.Basic.BasicLibrary;
import com.daw.pms.Service.PMS.LibraryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      List<BasicLibrary> pmsLibraries = libraryService.getLibraries(pmsId, 0);
      System.out.println("pmsLibraries: \n" + pmsLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicLibrary> qqmusicLibraries = libraryService.getLibraries(pmsId, 1);
      System.out.println("qqmusicLibraries: \n" + qqmusicLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicLibrary> ncmLibraries = libraryService.getLibraries(pmsId, 2);
      System.out.println("ncmLibraries: \n" + ncmLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      List<BasicLibrary> bilibiliLibraries = libraryService.getLibraries(pmsId, 3);
      System.out.println("bilibiliLibraries: \n" + bilibiliLibraries);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void getDetailLibrary() {
    try {
      BasicLibrary pmsLibrary = libraryService.getDetailLibrary("001", 0);
      System.out.println("pmsLibrary: \n" + pmsLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicLibrary qqmusicLibrary = libraryService.getDetailLibrary("8729577481", 1);
      System.out.println("qqmusicLibrary: \n" + qqmusicLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicLibrary ncmLibrary = libraryService.getDetailLibrary("8574846185", 2);
      System.out.println("ncmLibrary: \n" + ncmLibrary);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      BasicLibrary bilibiliLibrary = libraryService.getDetailLibrary("001", 3);
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
      Long pmsLibraryId = libraryService.createLibrary(library, 0);
      System.out.println("pmsLibraryId: \n" + pmsLibraryId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Long qqmusicLibraryId = libraryService.createLibrary(library, 1);
      System.out.println("qqmusicLibraryId: \n" + qqmusicLibraryId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Long ncmLibraryId = libraryService.createLibrary(library, 2);
      System.out.println("ncmLibraryId: \n" + ncmLibraryId);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Long bilibiliLibraryId = libraryService.createLibrary(library, 3);
      System.out.println("bilibiliLibraryId: \n" + bilibiliLibraryId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void deleteLibrary() {
    try {
      Map<String, Object> pmsDeletingResult = libraryService.deleteLibrary("1", 0);
      System.out.println("pmsDeletingResult: \n" + pmsDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> qqmusicDeletingResult = libraryService.deleteLibrary("1", 1);
      System.out.println("qqmusicDeletingResult: \n" + qqmusicDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> ncmDeletingResult = libraryService.deleteLibrary("1", 2);
      System.out.println("ncmDeletingResult: \n" + ncmDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> bilibiliDeletingResult = libraryService.deleteLibrary("1", 3);
      System.out.println("bilibiliDeletingResult: \n" + bilibiliDeletingResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void addSongsToLibrary() {
    try {
      Map<String, Object> pmsAddingSongsResult = libraryService.addSongsToLibrary("1", "1,2", 0);
      System.out.println("pmsAddingSongsResult: \n" + pmsAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> qqmusicAddingSongsResult =
          libraryService.addSongsToLibrary("1", "1,2", 1);
      System.out.println("qqmusicAddingSongsResult: \n" + qqmusicAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> ncmAddingSongsResult = libraryService.addSongsToLibrary("1", "1,2", 2);
      System.out.println("ncmAddingSongsResult: \n" + ncmAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> bilibiliAddingSongsResult =
          libraryService.addSongsToLibrary("1", "1,2", 3);
      System.out.println("bilibiliAddingSongsResult: \n" + bilibiliAddingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void moveSongsToOtherLibrary() {
    try {
      Map<String, Object> pmsMovingSongsResult =
          libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 0);
      System.out.println("pmsMovingSongsResult: \n" + pmsMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> qqmusicMovingSongsResult =
          libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 1);
      System.out.println("qqmusicMovingSongsResult: \n" + qqmusicMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> ncmMovingSongsResult =
          libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 2);
      System.out.println("ncmMovingSongsResult: \n" + ncmMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> bilibiliMovingSongsResult =
          libraryService.moveSongsToOtherLibrary("1,2", "1", "2", 3);
      System.out.println("bilibiliMovingSongsResult: \n" + bilibiliMovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  void removeSongsFromLibrary() {
    try {
      Map<String, Object> pmsRemovingSongsResult =
          libraryService.removeSongsFromLibrary("1", "1,2", 0);
      System.out.println("pmsRemovingSongsResult: \n" + pmsRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> qqmusicRemovingSongsResult =
          libraryService.removeSongsFromLibrary("1", "1,2", 1);
      System.out.println("qqmusicRemovingSongsResult: \n" + qqmusicRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> ncmRemovingSongsResult =
          libraryService.removeSongsFromLibrary("1", "1,2", 2);
      System.out.println("ncmRemovingSongsResult: \n" + ncmRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      Map<String, Object> bilibiliRemovingSongsResult =
          libraryService.removeSongsFromLibrary("1", "1,2", 3);
      System.out.println("bilibiliRemovingSongsResult: \n" + bilibiliRemovingSongsResult);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
