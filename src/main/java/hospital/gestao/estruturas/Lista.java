package hospital.gestao.estruturas;

import java.util.Comparator;

/**
 * Implementação de uma Lista Ligada Simples genérica
 * para substituir o uso de ArrayList.
 */
public class Lista<T> {
    private No<T> cabeca;
    private int tamanho;

    public Lista() {
        this.cabeca = null;
        this.tamanho = 0;
    }

    public void adicionar(T elemento) {
        No<T> novoNo = new No<>(elemento);
        if (cabeca == null) {
            cabeca = novoNo;
        } else {
            No<T> atual = cabeca;
            while (atual.getProximo() != null) {
                atual = atual.getProximo();
            }
            atual.setProximo(novoNo);
        }
        tamanho++;
    }

    public T obter(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice: " + indice + ", Tamanho: " + tamanho);
        }
        No<T> atual = cabeca;
        for (int i = 0; i < indice; i++) {
            atual = atual.getProximo();
        }
        return atual.getValor();
    }

    public boolean remover(T elemento) {
        if (cabeca == null)
            return false;

        if (cabeca.getValor().equals(elemento)) {
            cabeca = cabeca.getProximo();
            tamanho--;
            return true;
        }

        No<T> atual = cabeca;
        while (atual.getProximo() != null) {
            if (atual.getProximo().getValor().equals(elemento)) {
                atual.setProximo(atual.getProximo().getProximo());
                tamanho--;
                return true;
            }
            atual = atual.getProximo();
        }
        return false;
    }

    public T remover(int indice) {
        if (indice < 0 || indice >= tamanho) {
            throw new IndexOutOfBoundsException("Índice: " + indice + ", Tamanho: " + tamanho);
        }

        No<T> removido;
        if (indice == 0) {
            removido = cabeca;
            cabeca = cabeca.getProximo();
        } else {
            No<T> atual = cabeca;
            for (int i = 0; i < indice - 1; i++) {
                atual = atual.getProximo();
            }
            removido = atual.getProximo();
            atual.setProximo(removido.getProximo());
        }
        tamanho--;
        return removido.getValor();
    }

    public int tamanho() {
        return tamanho;
    }

    public boolean vazia() {
        return tamanho == 0;
    }

    // Algoritmo Bubble Sort para ordenar a lista
    public void set(int index, T elemento) {
        if (index < 0 || index >= tamanho) {
            throw new IndexOutOfBoundsException("Índice inválido: " + index);
        }
        No<T> atual = cabeca;
        for (int i = 0; i < index; i++) {
            atual = atual.getProximo();
        }
        atual.setValor(elemento);
    }

    public void ordenar(Comparator<T> comparator) {
        if (tamanho <= 1)
            return;

        boolean trocou;
        do {
            trocou = false;
            No<T> atual = cabeca;
            No<T> anterior = null;

            while (atual != null && atual.getProximo() != null) {
                No<T> proximo = atual.getProximo();

                if (comparator.compare(atual.getValor(), proximo.getValor()) > 0) {
                    // Troca os valores (mais simples que trocar os nós)
                    T temp = atual.getValor();
                    atual.setValor(proximo.getValor());
                    proximo.setValor(temp);
                    trocou = true;
                }
                atual = proximo;
            }
        } while (trocou);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        No<T> atual = cabeca;
        while (atual != null) {
            sb.append(atual.getValor());
            if (atual.getProximo() != null) {
                sb.append(", ");
            }
            atual = atual.getProximo();
        }
        sb.append("]");
        return sb.toString();
    }
}
