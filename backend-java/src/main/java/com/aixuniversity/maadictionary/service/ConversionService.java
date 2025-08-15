package com.aixuniversity.maadictionary.service;

import com.aixuniversity.maadictionary.dao.normal.OrthographyVariantDao;
import com.aixuniversity.maadictionary.dao.normal.VocabularyDao;
import com.aixuniversity.maadictionary.model.OrthographyVariant;
import com.aixuniversity.maadictionary.model.Vocabulary;
import com.aixuniversity.maadictionary.parser.extractors.IPAExtractor;
import com.aixuniversity.maadictionary.service.conversion.IPA2Orthography;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ConversionService {

    public static String toOrthographyKeepingPunct(String text, String orthography) {
        // 3 classes de tokens : (1) espaces  (2) ponctuation  (3) tout le reste (= “mots” IPA)
        // On garde tout tel quel sauf (3) qu'on convertit.
        Pattern TOKENS = Pattern.compile("(\\s+|[\\p{Punct}“”‘’…—–]+|[^\\s\\p{Punct}“”‘’…—–]+)");
        Matcher m = TOKENS.matcher(text);
        StringBuilder out = new StringBuilder();

        while (m.find()) {
            String tok = m.group(1);

            // espaces : on recopie
            if (tok.codePoints().allMatch(Character::isWhitespace)) {
                out.append(tok);
                continue;
            }
            // ponctuation : on recopie
            if (tok.matches("[\\p{Punct}“”‘’…—–]+")) {
                out.append(tok);
                continue;
            }

            // sinon: c'est un “mot” IPA → on convertit
            try {
                String ipa = IPAExtractor.parseIPA(tok);
                List<IPA2Orthography.Path> paths = IPA2Orthography.convert(orthography, ipa);
                String form = (!paths.isEmpty() ? paths.getFirst().form()    // meilleur chemin
                        : tok);                  // fallback: recopie si rien
                out.append(form);
            } catch (Exception e) {
                // fallback ultra-sûr
                out.append(tok);
            }
        }
        return out.toString();
    }


    public static String getMainOrthography(int id, String ipa, String orthography) {
        try {
            Vocabulary v = new VocabularyDao().searchById(id);
            if (OrthographyVariant.getVariants().containsKey(v) && OrthographyVariant.getVariants().get(v).containsKey(ipa) && v != null)
                return OrthographyVariant.getVariantsByVocabulary(v).get(ipa).getForm();
            else if (v != null) {
                List<OrthographyVariant> orthos = new OrthographyVariantDao().getAllFromVocId(id);
                if (!orthos.isEmpty() && orthos.stream().anyMatch(o -> o.getScript().equalsIgnoreCase(orthography))) {
                    OrthographyVariant ortho = orthos.stream().filter(o -> o.getScript().equalsIgnoreCase(orthography)).findFirst().get();
                    OrthographyVariant.addVariant(ortho);
                    return ortho.getForm();
                } else {
                    var form = IPA2Orthography.convert(orthography, ipa);
                    if (!form.isEmpty()) {
                        OrthographyVariant ortho = new OrthographyVariant(v, form.getFirst().form(), orthography);
                        OrthographyVariant.addVariant(ortho);
                        return ortho.getForm();
                    } else {
                        System.err.println("Orthography not found: " + ipa + " for " + v.getEntry());
                        return new OrthographyVariant(v, "Orthography not found.", orthography).getForm();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
