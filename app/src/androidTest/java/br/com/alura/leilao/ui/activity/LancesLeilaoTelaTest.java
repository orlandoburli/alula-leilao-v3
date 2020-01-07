package br.com.alura.leilao.ui.activity;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import br.com.alura.leilao.BaseTesteIntegracao;
import br.com.alura.leilao.R;
import br.com.alura.leilao.database.dao.UsuarioDAO;
import br.com.alura.leilao.model.Usuario;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;

public class LancesLeilaoTelaTest extends BaseTesteIntegracao {

    @Rule
    public ActivityTestRule<ListaLeilaoActivity> activityTestRule
            = new ActivityTestRule<> ( ListaLeilaoActivity.class, true, false );

    @Before
    public void setup() throws IOException {
        this.limpaDados ();
    }

    @Test
    public void deve_AtualizarLancesDoLeilao_QuandoReceberUmLance() throws IOException {

        // Salva leilão na API
        tentaSalvarLeilaoNaApi ( "Carro" );

        // Abre Activity
        activityTestRule.launchActivity ( new Intent () );

        // Clica no leilão
        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .perform ( RecyclerViewActions.actionOnItemAtPosition ( 0, click () ) );

        // Clica no fab da tela de lances do leilão
        onView ( withId ( R.id.lances_leilao_fab_adiciona ) )
                .perform ( click () );

        // Verifica se aparece o dialog para adicionar usuários
        onView ( withText ( "Usuários não encontrados" ) )
                .check ( matches ( isDisplayed () ) );

        onView ( withText ( "Não existe usuários cadastrados! Cadastre um usuário para propor o lance." ) )
                .check ( matches ( isDisplayed () ) );

        // Clica no botão para cadastrar usuário
        onView ( withText ( "Cadastrar usuário" ) )
                .check ( matches ( isDisplayed () ) )
                .perform ( click () );

        // Clica no fab de adicionar usuário
        onView ( allOf (
                withId ( R.id.lista_usuario_fab_adiciona ),
                isDisplayed () )
        ).perform ( click () );

        // Clica on input do nome do usuário e digita "Alex"
        onView ( allOf (
                withId ( R.id.form_usuario_nome_text ),
                isDisplayed () )
        )
                .perform ( click () )
                .perform ( typeText ( "Alex" ), closeSoftKeyboard () );

        // Clica no botão "Adicionar" para confirmar a adição do usuário
        onView ( allOf (
                withId ( android.R.id.button1 ),
                withText ( "Adicionar" ),
                isDisplayed () )
        ).perform ( scrollTo (), click () );

        // Volta para a tela de lances do leilão
        pressBack ();

        // Clica no fab para adicionar o leilão
        propoeNovoLance ( 1, "Alex", "200" );

        // Fazer assertions para as views de maior e menor lances

        // Check no label de maior lance
        onView ( withId ( R.id.lances_leilao_maior_lance ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 200 ) )
                ) ) );

        // Check no label de menor lance
        onView ( withId ( R.id.lances_leilao_menor_lance ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 200 ) )
                ) ) );

        // Check no label de maiores lances
        onView ( withId ( R.id.lances_leilao_maiores_lances ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 200 ) + " - (1) Alex\n" )
                ) ) );
    }

    @Test
    public void deve_AtualizarLancesDoLeilao_QuandoReceberTresLances() throws IOException {
        // Salva leilão na API
        tentaSalvarLeilaoNaApi ( "Carro" );

        tentaSalvarUsuariosNoBancoDeDados (
                new Usuario ( "Alex" ),
                new Usuario ( "Fran" )
        );

        // Abre Activity
        activityTestRule.launchActivity ( new Intent () );

        // Clica no leilão
        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .perform ( RecyclerViewActions.actionOnItemAtPosition ( 0, click () ) );

        propoeNovoLance ( 1, "Alex", "200" );

        // Clica no fab para adicionar o leilão
        propoeNovoLance ( 2, "Fran", "300" );

        // Clica no fab para adicionar o leilão
        propoeNovoLance ( 1, "Alex", "400" );

        // Fazer assertions para as views de maior e menor lances

        // Check no label de maior lance
        onView ( withId ( R.id.lances_leilao_maior_lance ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 400 ) )
                ) ) );

        // Check no label de menor lance
        onView ( withId ( R.id.lances_leilao_menor_lance ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 200 ) )
                ) ) );

        // Check no label de maiores lances
        onView ( withId ( R.id.lances_leilao_maiores_lances ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 400 ) + " - (1) Alex\n" +
                                formatadorDeMoeda.formata ( 300 ) + " - (2) Fran\n" +
                                formatadorDeMoeda.formata ( 200 ) + " - (1) Alex\n" )
                ) ) );
    }

    @Test
    public void deve_AtualizarLancesDoLeilao_QuandoReceberUmLanceMuitoAlto() throws IOException {
        // Salva leilão na API
        tentaSalvarLeilaoNaApi ( "Carro" );

        tentaSalvarUsuariosNoBancoDeDados ( new Usuario ( "Alex" ) );

        // Abre Activity
        activityTestRule.launchActivity ( new Intent () );

        // Clica no leilão
        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .perform ( RecyclerViewActions.actionOnItemAtPosition ( 0, click () ) );

        propoeNovoLance ( 1, "Alex", "2000000000" );

        // Fazer assertions para as views de maior e menor lances

        // Check no label de maior lance
        onView ( withId ( R.id.lances_leilao_maior_lance ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 2000000000 ) )
                ) ) );

        // Check no label de menor lance
        onView ( withId ( R.id.lances_leilao_menor_lance ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 2000000000 ) )
                ) ) );

        // Check no label de maiores lances
        onView ( withId ( R.id.lances_leilao_maiores_lances ) )
                .check ( matches ( allOf (
                        isDisplayed (),
                        withText ( formatadorDeMoeda.formata ( 2000000000 ) + " - (1) Alex\n" )
                ) ) );
    }

    private void propoeNovoLance(int idUsuario, String usuario, String valor) {
        // Clica no fab para adicionar o leilão
        onView ( withId ( R.id.lances_leilao_fab_adiciona ) )
                .check ( matches ( isDisplayed () ) )
                .perform ( click () );

        // Verifica visibilidade do dialog com o título esperado
        onView ( withText ( "Novo lance" ) )
                .check ( matches ( isDisplayed () ) );

        // Clica no input e preenche o valor de 200 reais
        onView ( withId ( R.id.form_lance_valor_text ) )
                .perform ( click (), typeText ( valor ), closeSoftKeyboard () );

        // Seleciona o usuário
        onView ( withId ( R.id.form_lance_usuario ) )
                .perform ( click () );

        // Seleciona o item do usuário Alex, fazendo um click no item do spinner
        onData ( is ( new Usuario ( idUsuario, usuario ) ) )
                .inRoot ( isPlatformPopup () )
                .perform ( click () );

        // Clica no botão propor
        onView ( withText ( "Propor" ) )
                .perform ( click () );
    }

    private void tentaSalvarUsuariosNoBancoDeDados(Usuario... usuarios) {
        UsuarioDAO dao = new UsuarioDAO ( InstrumentationRegistry.getTargetContext () );

        for (Usuario usuario : usuarios) {
            assertNotNull ( "Usuário " + usuario.getNome () + " não foi salvo", dao.salva ( usuario ) );
        }
    }

    @After
    public void tearDown() throws IOException {
        this.limpaDados ();
    }
}