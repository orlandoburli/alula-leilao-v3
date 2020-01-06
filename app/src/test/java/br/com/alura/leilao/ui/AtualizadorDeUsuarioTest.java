package br.com.alura.leilao.ui;

import android.support.v7.widget.RecyclerView;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import br.com.alura.leilao.database.dao.UsuarioDAO;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.ui.recyclerview.adapter.ListaUsuarioAdapter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AtualizadorDeUsuarioTest {

    @Mock
    private UsuarioDAO dao;

    @Mock
    private ListaUsuarioAdapter adapter;

    @Mock
    private RecyclerView recyclerView;

    @Test
    public void deve_AtualizarListaDeUsuarios_QuandoSalvarUsuario() {
        AtualizadorDeUsuario atualizadorDeUsuario = new AtualizadorDeUsuario ( dao, adapter, recyclerView );

        Usuario alex = new Usuario ( "Alex" );

        when ( dao.salva ( alex ) ).thenReturn ( new Usuario ( 1, "Alex" ) );
        when ( adapter.getItemCount () ).thenReturn ( 1 );

        atualizadorDeUsuario.salva ( alex );

        verify ( dao ).salva ( alex );
        verify ( adapter ).adiciona ( new Usuario ( 1, "Alex" ) );
        verify ( recyclerView ).smoothScrollToPosition ( 0 );
    }
}