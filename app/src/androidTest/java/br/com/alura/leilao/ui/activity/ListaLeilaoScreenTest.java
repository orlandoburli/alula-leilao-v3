package br.com.alura.leilao.ui.activity;

import android.content.Intent;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import br.com.alura.leilao.R;
import br.com.alura.leilao.api.retrofit.client.TesteWebClient;
import br.com.alura.leilao.formatter.FormatadorDeMoeda;
import br.com.alura.leilao.model.Leilao;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
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
    private final TesteWebClient leilaoWebClient = new TesteWebClient ();
    private final FormatadorDeMoeda formatadorDeMoeda = new FormatadorDeMoeda ();
    @Rule
    public ActivityTestRule<ListaLeilaoActivity> activityTestRule
            = new ActivityTestRule<> ( ListaLeilaoActivity.class, true, false );

    @Before
    public void setup() throws IOException {
        this.limpaDados ();
    }

    @Test
    public void deve_AparecerUmLeilao_QuandoCarregarUmLeilaoNaApi() throws IOException {

        tentaSalvarLeilaoNaApi ( "Carro" );

        String maiorLanceEsperado = formatadorDeMoeda.formata ( 0.0 );

        activityTestRule.launchActivity ( new Intent () );

        onView ( allOf (
                withText ( "Carro" ),
                withId ( R.id.item_leilao_descricao )
        ) )
                .check ( matches ( isDisplayed () ) );

        onView ( allOf (
                withText ( maiorLanceEsperado ),
                withId ( R.id.item_leilao_maior_lance )
        ) )
                .check ( matches ( isDisplayed () ) );

        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .check ( matches ( apareceLeilao ( 0, "Carro", 0.00 ) ) );
    }

    @Test
    public void deve_AparecerDoisLeiloes_QuandoCarregarDoisLeiloesDaApi() throws IOException {

        tentaSalvarLeilaoNaApi ( "Carro", "Computador" );

        activityTestRule.launchActivity ( new Intent () );

        // Testes no carro

        onView ( allOf (
                withText ( "Carro" ),
                withId ( R.id.item_leilao_descricao )
        ) )
                .check ( matches ( isDisplayed () ) );

        // Usando o hasSibling para procurar pelo valor R$ 0,00 perto da palavra "Carro"
        onView ( allOf (
                withText ( formatadorDeMoeda.formata ( 0.0 ) ),
                withId ( R.id.item_leilao_maior_lance ),
                hasSibling ( allOf (
                        withText ( "Carro" ),
                        withId ( R.id.item_leilao_descricao )
                ) )
        ) )
                .check ( matches ( isDisplayed () ) );

        // Testes no computador

        onView ( allOf (
                withText ( "Computador" ),
                withId ( R.id.item_leilao_descricao )
        ) )
                .check ( matches ( isDisplayed () ) );

        // Usando o hasSibling para procurar pelo valor R$ 0,00 perto da palavra "Computador"
        onView ( allOf (
                withText ( formatadorDeMoeda.formata ( 0.0 ) ),
                withId ( R.id.item_leilao_maior_lance ),
                hasSibling ( allOf (
                        withText ( "Computador" ),
                        withId ( R.id.item_leilao_descricao )
                ) )
        ) )
                .check ( matches ( isDisplayed () ) );

        // Usando matcher personalizado para validar tanto o computador quando o carro
        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .check ( matches ( apareceLeilao ( 0, "Carro", 0.00 ) ) );

        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .check ( matches ( apareceLeilao ( 1, "Computador", 0.00 ) ) );
    }

    private Matcher<? super View> apareceLeilao(final int posicao,
                                                final String descricaoEsperada,
                                                final double maiorLanceEsperado) {


        return new BoundedMatcher<View, RecyclerView> ( RecyclerView.class ) {

            private Matcher<View> displayed = isDisplayed ();

            @Override
            public void describeTo(Description description) {
                description.appendText ( "View com descrição " )
                        .appendValue ( descricaoEsperada )
                        .appendText ( ", maior lance " )
                        .appendValue ( formatadorDeMoeda.formata ( maiorLanceEsperado ) )
                        .appendText ( " na posição " )
                        .appendValue ( posicao )
                        .appendText ( " não foi encontrada" );
            }

            @Override
            protected boolean matchesSafely(RecyclerView item) {
                RecyclerView.ViewHolder viewHolder = item.findViewHolderForAdapterPosition ( posicao );

                if (viewHolder == null) {
                    throw new IndexOutOfBoundsException ( "View na posição " + posicao + " não foi encontrado" );
                }

                View viewDoViewHolder = viewHolder.itemView;

                TextView textViewDescricao = viewDoViewHolder.findViewById ( R.id.item_leilao_descricao );

                boolean temDescricaoEsperada = textViewDescricao.getText ()
                        .toString ().equals ( descricaoEsperada );

                TextView textViewMaiorLance = viewDoViewHolder.findViewById ( R.id.item_leilao_maior_lance );

                FormatadorDeMoeda formatador = new FormatadorDeMoeda ();

                boolean temMaiorLanceEsperado = textViewMaiorLance.getText ().toString ()
                        .equals ( formatador.formata ( maiorLanceEsperado ) );


                return temDescricaoEsperada && temMaiorLanceEsperado && displayed.matches ( viewDoViewHolder );

            }
        };
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