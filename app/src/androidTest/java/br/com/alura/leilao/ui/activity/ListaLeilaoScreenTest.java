package br.com.alura.leilao.ui.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import br.com.alura.leilao.R;
import br.com.alura.leilao.api.retrofit.client.TesteWebClient;
import br.com.alura.leilao.model.Leilao;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ListaLeilaoScreenTest {

    public static final String MSG_BANCO_DE_DADOS_NÃO_FOI_LIMPO = "Banco de dados não foi limpo!";
    public static final String MSG_LEILAO_NAO_FOI_SALVO = "Leilão não foi salvo";

    @Rule
    public ActivityTestRule<ListaLeilaoActivity> activityTestRule
            = new ActivityTestRule<> ( ListaLeilaoActivity.class, true, false );

    private TesteWebClient leilaoWebClient = new TesteWebClient ();

    @Before
    public void setup() throws IOException {
        this.limpaDados ();
    }

    @Test
    public void deve_AparecerUmLeilao_QuandoCarregarUmLeilaoNaApi() throws IOException {

        tentaSalvarLeilaoNaApi ( "Carro" );

        activityTestRule.launchActivity ( new Intent () );

        onView ( allOf (
                withText ( "Carro" ),
                withId ( R.id.item_leilao_descricao )
        ) )
                .check ( matches ( isDisplayed () ) );
    }

    @Test
    public void deve_AparecerDoisLeiloes_QuandoCarregarDoisLeiloesDaApi() throws IOException {

        tentaSalvarLeilaoNaApi ( "Carro", "Computador" );

        activityTestRule.launchActivity ( new Intent () );

        onView ( allOf (
                withText ( "Carro" ),
                withId ( R.id.item_leilao_descricao )
        ) )
                .check ( matches ( isDisplayed () ) );

        onView ( allOf (
                withText ( "Computador" ),
                withId ( R.id.item_leilao_descricao )
        ) )
                .check ( matches ( isDisplayed () ) );
    }

    @After
    public void tearDown() throws IOException {
        this.limpaDados ();
    }

    private void limpaDados() throws IOException {
        if (!leilaoWebClient.limpaDados ())
            fail ( MSG_BANCO_DE_DADOS_NÃO_FOI_LIMPO );
    }

    private void tentaSalvarLeilaoNaApi(String... leiloes) throws IOException {
        for (String nome : leiloes) {
            Leilao leilao = leilaoWebClient.salva ( new Leilao ( nome ) );
            assertNotNull ( MSG_LEILAO_NAO_FOI_SALVO, leilao );
        }
    }
}