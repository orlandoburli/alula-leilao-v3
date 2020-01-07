package br.com.alura.leilao.ui.activity;


import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import br.com.alura.leilao.BaseTesteIntegracao;
import br.com.alura.leilao.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ListaUsuarioTelaTest extends BaseTesteIntegracao {

    @Rule
    public ActivityTestRule<ListaLeilaoActivity> mActivityTestRule = new ActivityTestRule<> ( ListaLeilaoActivity.class );

    @Before
    public void setup() throws IOException {
        this.limpaDados ();
    }

    @Test
    public void deve_AparecerUsuarioNaListaDeUsuarios_QuandoCadastrarUmUsuario() {
        onView ( allOf (
                withId ( R.id.lista_leilao_menu_usuarios ),
                withContentDescription ( "Usuários" ),
                isDescendantOfA ( withId ( R.id.action_bar ) ),
                isDisplayed () )
        ).perform ( click () );

        onView ( allOf (
                withId ( R.id.lista_usuario_fab_adiciona ),
                isDisplayed () )
        ).perform ( click () );

        onView ( allOf (
                withId ( R.id.form_usuario_nome_text ),
                isDisplayed () )
        )
                .perform ( click () )
                .perform ( replaceText ( "Alex" ), closeSoftKeyboard () );

        onView ( allOf (
                withId ( android.R.id.button1 ),
                withText ( "Adicionar" ) )
        ).perform ( scrollTo (), click () );

        onView ( allOf (
                withId ( R.id.item_usuario_id_com_nome ),
                isDisplayed () ) )
                .check ( matches ( withText ( "(1) Alex" ) ) );
    }

    @After
    public void tearDown() throws IOException {
        this.limpaDados ();
    }
}