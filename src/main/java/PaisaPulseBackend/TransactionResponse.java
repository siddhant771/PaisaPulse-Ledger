package PaisaPulseBackend;



import com.fasterxml.jackson.annotation.JsonProperty;

public record TransactionResponse(

        double amount,

        String merchant,

        String category,

        @JsonProperty("is_debit")
        boolean isDebit,

        String date,

        @JsonProperty("confidence_score")
        double confidenceScore

) {}