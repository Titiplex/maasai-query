package com.aixuniversity.maaweb.dto;

import java.util.List;

public record PageDto<T>(
        long total,   // nombre total de hits
        int page,     // index courant (0-based)
        int size,     // taille demandée
        List<T> items
) {
}
