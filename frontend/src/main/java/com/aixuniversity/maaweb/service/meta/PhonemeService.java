package com.aixuniversity.maaweb.service.meta;

import com.aixuniversity.maadictionary.dao.normal.PhonemeDao;
import com.aixuniversity.maadictionary.model.Phoneme;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class PhonemeService {

    private final PhonemeDao dao = new PhonemeDao();

    public List<String> listCodes() {
        try {
            return dao.getAll().stream().map(Phoneme::getCode).toList();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load phoneme-code list", e);
        }
    }

    public List<String> listIpa() {
        try {
            return dao.getAll().stream().map(Phoneme::getIpa).toList();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot load phoneme list", e);
        }
    }
}
