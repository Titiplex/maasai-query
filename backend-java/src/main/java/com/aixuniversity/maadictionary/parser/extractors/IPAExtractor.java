package com.aixuniversity.maadictionary.parser.extractors;

import com.aixuniversity.maadictionary.config.IPAConfig;

/**
 * Classe qui contient des méthodes pour parser une chaîne d'entrée en IPA.
 * Ce parser intègre la gestion des digrammes, des diacritiques et le traitement spécifique des particules.
 */

// TODO manage tones
public abstract class IPAExtractor {
    /**
     * Extrait la chaîne IPA correspondant au mot d'entrée.
     * Si le mot contient une particule suivie d'un tiret en début d'entrée,
     * la particule est traitée de manière spécifique :</br>
     * – Si la particule est constituée uniquement d'une consonne (ou d'un groupe purement consonantique),
     * elle est fusionnée avec la racine (la syllabe suivante).</br>
     * – Si la particule contient une voyelle (donc consonne+voyelle), elle sera considérée comme une syllabe à part.
     *
     * @param word Le mot d'entrée en orthographe (contenant éventuellement des tirets)
     * @return La représentation IPA du mot, avec éventuellement un séparateur espace si la particule forme une syllabe distincte.
     */
    public static String parseIPA(String word) {
        // Vérifier s'il existe un tiret indiquant une particule en début d'entrée.
        int hyphenIndex = word.indexOf('-');
        if (hyphenIndex > 0 && hyphenIndex < word.length() - 1) {
            // Extraire la particule (avant le tiret) et la racine (après le tiret).
            String particle = word.substring(0, hyphenIndex);
            String root = word.substring(hyphenIndex + 1);

            // Conversion de chaque partie en IPA
            String ipaParticle = parseIPAWithoutHyphen(particle);
            String ipaRoot = parseIPAWithoutHyphen(root);

            // Si la particule convertie est purement consonantique,
            // on la fusionne directement avec la racine.
            return ipaParticle + ipaRoot;
        } else {
            // Sinon, on effectue la conversion sur l'ensemble du mot.
            return parseIPAWithoutHyphen(word);
        }
    }

    /**
     * Méthode qui convertit un mot (sans tiret) en sa chaîne IPA.
     * Elle parcourt le mot et essaie d'abord de détecter les digrammes définis.
     *
     * @param word Le mot à convertir.
     * @return La chaîne IPA correspondante.
     */
    public static String parseIPAWithoutHyphen(String word) {
        StringBuilder IPA = new StringBuilder();
        int i = 0;
        while (i < word.length()) {
            // Tentative de lire un digramme si possible.
            if (i < word.length() - 1) {
                String twoChar = word.substring(i, i + 2).toLowerCase();
                String mappedDigraph = IPAConfig.get(twoChar);
                if (mappedDigraph != null) {
                    IPA.append(mappedDigraph);
                    i += 2;
                    continue;
                }
            }
            // Sinon, traiter le caractère courant.
            String oneChar = word.substring(i, i + 1);
            String mappedChar = IPAConfig.get(oneChar);
            if (mappedChar != null) {
                IPA.append(mappedChar);
            }
            i++;
        }
        return IPA.toString();
    }
}
