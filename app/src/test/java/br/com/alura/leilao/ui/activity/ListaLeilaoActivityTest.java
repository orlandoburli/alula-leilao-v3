package br.com.alura.leilao.ui.activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;

import br.com.alura.leilao.api.retrofit.client.LeilaoWebClient;
import br.com.alura.leilao.api.retrofit.client.RespostaListener;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.ui.AtualizadorDeLeiloes;
import br.com.alura.leilao.ui.AtualizadorDeLeiloes.ErroCarregaLeiloesListener;
import br.com.alura.leilao.ui.recyclerview.adapter.ListaLeilaoAdapter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListaLeilaoActivityTest {

    @Mock
    private LeilaoWebClient client;

    @Mock
    private ListaLeilaoAdapter adapter;

    @Mock
    private ErroCarregaLeiloesListener listener;

    @Test
    public void deveAtualizarListaDeLeilosQuandoBuscarLeiloesDaApi() {
        AtualizadorDeLeiloes atualizadorDeLeiloes = new AtualizadorDeLeiloes ();

        doAnswer ( new Answer () {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RespostaListener<List<Leilao>> argument = invocation.getArgument ( 0 );
                argument.sucesso ( Arrays.asList (
                        new Leilao ( "Computador" ),
                        new Leilao ( "Carro" ),
                        new Leilao ( "Celular" )
                ) );
                return null;
            }
        } ).when ( client ).todos ( ArgumentMatchers.any ( RespostaListener.class ) );

        atualizadorDeLeiloes.buscaLeiloes ( adapter, client, listener );

        verify ( client ).todos ( ArgumentMatchers.any ( RespostaListener.class ) );
        verify ( adapter ).atualiza ( Arrays.asList (
                new Leilao ( "Computador" ),
                new Leilao ( "Carro" ),
                new Leilao ( "Celular" )
        ) );
    }

    @Test
    public void deveApresentarMensagemDeFalhaQuandoFalharABuscaDeLeiloes() {
        AtualizadorDeLeiloes atualizadorDeLeiloes = (new AtualizadorDeLeiloes ());

        doAnswer ( new Answer () {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RespostaListener<List<Leilao>> argument = invocation.getArgument ( 0 );
                argument.falha ( Mockito.anyString () );
                return null;
            }
        } ).when ( client ).todos ( ArgumentMatchers.any ( RespostaListener.class ) );

        atualizadorDeLeiloes.buscaLeiloes ( adapter, client, listener );

        verify ( listener ).erroAoCarregar ( anyString () );
    }
}