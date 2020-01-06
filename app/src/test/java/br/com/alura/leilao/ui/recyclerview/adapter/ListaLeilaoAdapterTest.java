package br.com.alura.leilao.ui.recyclerview.adapter;

import android.content.Context;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.alura.leilao.model.Leilao;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

public class ListaLeilaoAdapterTest {

    @Mock
    Context context;

    @Spy
    ListaLeilaoAdapter adapter = Mockito.spy ( new ListaLeilaoAdapter ( context ) );

    @Test
    public void deveAtualizarListaDeLeiloesQuandoReceberListaDeLeiloes() {
        doNothing ().when ( adapter ).atualizaLista ();

        adapter.atualiza ( new ArrayList<Leilao> ( Arrays.asList (
                new Leilao ( "Console" ),
                new Leilao ( "Computador" )
        ) ) );

        // Verifica se o método foi chamado
        verify ( adapter ).atualizaLista ();

        assertThat ( adapter.getItemCount (), is ( 2 ) );
    }
}
