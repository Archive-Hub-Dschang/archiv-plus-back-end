package com.lde.usermicroservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class PaginationDTO {
    private int page;           // Numéro de la page actuelle (1-based)
    private int limit;          // Nombre d'éléments par page
    private long total;         // Total d'éléments
    private int totalPages;     // Nombre total de pages
    private boolean hasMore;    // S'il y a une page suivante
    private boolean hasPrevious;// S'il y a une page précédente


}
