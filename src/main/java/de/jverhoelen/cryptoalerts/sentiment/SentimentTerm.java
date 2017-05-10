package de.jverhoelen.cryptoalerts.sentiment;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

@Entity
public class SentimentTerm {

    @Enumerated(value = EnumType.STRING)
    private SentimentTermKind kind;

    @Id
    private String term;

    public SentimentTerm(SentimentTermKind kind, String term) {
        this.kind = kind;
        this.term = term;
    }

    public SentimentTerm() {
    }

    public SentimentTermKind getKind() {
        return kind;
    }

    public void setKind(SentimentTermKind kind) {
        this.kind = kind;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SentimentTerm that = (SentimentTerm) o;

        return term != null ? term.equals(that.term) : that.term == null;
    }

    @Override
    public int hashCode() {
        return term != null ? term.hashCode() : 0;
    }
}
