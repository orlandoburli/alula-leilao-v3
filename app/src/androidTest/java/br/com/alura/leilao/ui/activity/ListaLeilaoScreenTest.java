package br.com.alura.leilao.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
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

import br.com.alura.leilao.BaseTesteIntegracao;
import br.com.alura.leilao.BuildConfig;
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
public class ListaLeilaoScreenTest extends BaseTesteIntegracao {

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

    @Test
    public void deve_AparecerUltimoLeilao_QuandoCarregarDezLeiloesDaApi() throws IOException {
        tentaSalvarLeilaoNaApi (
                "Carro",
                "Computador",
                "TV",
                "Notebook",
                "Console",
                "Jogo",
                "Estante",
                "Quadro",
                "Smartphone",
                "Casa" );

        activityTestRule.launchActivity ( new Intent () );

        onView ( withId ( R.id.lista_leilao_recyclerview ) )
                .perform ( RecyclerViewActions.scrollToPosition ( 9 ) )
                .check ( matches ( apareceLeilao ( 9, "Casa", 0.00 ) ) );

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

                boolean temDescricaoEsperada  = apareceDescricaoEsperada ( viewDoViewHolder );
                boolean temMaiorLanceEsperado = apareceMaiorLanceEsperado ( viewDoViewHolder );

                return temDescricaoEsperada &&
                        temMaiorLanceEsperado &&
                        displayed.matches ( viewDoViewHolder );
            }

            private boolean apareceMaiorLanceEsperado(View viewDoViewHolder) {
                TextView textViewMaiorLance = viewDoViewHolder.findViewById ( R.id.item_leilao_maior_lance );

                return textViewMaiorLance.getText ().toString ()
                        .equals ( formatadorDeMoeda.formata ( maiorLanceEsperado ) )
                        &&
                        displayed.matches ( textViewMaiorLance );
            }

            private boolean apareceDescricaoEsperada(View viewDoViewHolder) {
                TextView textViewDescricao = viewDoViewHolder.findViewById ( R.id.item_leilao_descricao );

                return textViewDescricao.getText ()
                        .toString ().equals ( descricaoEsperada )
                        &&
                        displayed.matches ( textViewDescricao );
            }
        };
    }

    @After
    public void tearDown() throws IOException {
        this.limpaDados ();
    }
}