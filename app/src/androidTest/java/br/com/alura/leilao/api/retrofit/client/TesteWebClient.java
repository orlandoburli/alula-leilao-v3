package br.com.alura.leilao.api.retrofit.client;

import java.io.IOException;

import br.com.alura.leilao.api.retrofit.RetrofitInicializador;
import br.com.alura.leilao.api.retrofit.TesteRetrofitInicionalizador;
import br.com.alura.leilao.api.retrofit.service.LeilaoService;
import br.com.alura.leilao.api.retrofit.service.TesteService;
import br.com.alura.leilao.model.Leilao;
import retrofit2.Call;
import retrofit2.Response;

public class TesteWebClient extends WebClient{

    private final TesteService service;

    public TesteWebClient() {
        service = new TesteRetrofitInicionalizador ().getTesteService();
    }

    public Leilao salva(Leilao leilao) throws IOException {
        Call<Leilao> call = this.service.salva ( leilao );

        Response<Leilao> leilaoResponse = call.execute ();

        if (temDados ( leilaoResponse )) {
            return leilaoResponse.body ();
        }

        return null;
    }

    public boolean limpaDados() throws IOException {
        return this.service.limpaBancoDeDados ().execute ().isSuccessful ();
    }
}
