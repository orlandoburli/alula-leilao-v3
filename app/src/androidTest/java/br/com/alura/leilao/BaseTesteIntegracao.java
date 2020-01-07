package br.com.alura.leilao;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import java.io.IOException;

import br.com.alura.leilao.api.retrofit.client.TesteWebClient;
import br.com.alura.leilao.formatter.FormatadorDeMoeda;
import br.com.alura.leilao.model.Leilao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public abstract class BaseTesteIntegracao {

    private final TesteWebClient leilaoWebClient = new TesteWebClient ();

    public static final String MSG_BANCO_DE_DADOS_NÃO_FOI_LIMPO = "Banco de dados não foi limpo!";
    public static final String MSG_LEILAO_NAO_FOI_SALVO = "Leilão não foi salvo";
    public final FormatadorDeMoeda formatadorDeMoeda = new FormatadorDeMoeda ();

    public void limpaDados() throws IOException {
        if (!leilaoWebClient.limpaDados ())
            fail ( MSG_BANCO_DE_DADOS_NÃO_FOI_LIMPO );

        Context context = InstrumentationRegistry.getTargetContext ();
        context.deleteDatabase ( BuildConfig.BANCO_DE_DADOS );
    }

    public void tentaSalvarLeilaoNaApi(String... leiloes) throws IOException {
        for (String nome : leiloes) {
            Leilao leilao = leilaoWebClient.salva ( new Leilao ( nome ) );
            assertNotNull ( MSG_LEILAO_NAO_FOI_SALVO, leilao );
        }
    }

    public void sleep(int millis) {
        try {
            Thread.sleep ( millis );
        } catch (InterruptedException e) {
            e.printStackTrace ();
        }
    }
}
