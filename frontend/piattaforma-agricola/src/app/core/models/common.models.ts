/**
 * Modelli comuni per la paginazione e altre strutture generiche.
 */

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// DTO per prodotti disponibili nei pacchetti
export interface ProdottoSummaryDTO {
  id: number;
  nome: string;
  descrizione?: string;
  prezzo: number;
  categoria?: string;
  quantitaDisponibile: number;
  unitaMisura?: string;
  luogoOrigine?: string;
  produttore?: {
    id: number;
    nomeAzienda: string;
  };
  certificazioni?: string[];
  stato: string;
  immagineUrl?: string;
}