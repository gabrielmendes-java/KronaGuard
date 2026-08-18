package com.garantia_facil.app.services;

import com.garantia_facil.app.models.Garantia;
import com.garantia_facil.app.repositories.GarantiaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class GarantiaService {
    private final GarantiaRepository garantiaRepository;

    public GarantiaService(GarantiaRepository garantiaRepository){
        this.garantiaRepository = garantiaRepository;
    }
    public Garantia criarGarantia(Garantia garantia){
        String codigoUnico = "GAR-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        garantia.setCodigoGarantia(codigoUnico);

        LocalDate validade = garantia.getDataRealizacao().plusDays(garantia.getDiasGarantia());
        garantia.setDataValidade(validade);

        return garantiaRepository.save(garantia);
    }

    public Garantia buscarPorCodigo(String codigo){
        return garantiaRepository.findByCodigoGarantia(codigo)
                .orElseThrow(() -> new RuntimeException("Garantia não encontrada."));
    }

    public List<Garantia> listarOuFiltrar(String termo){
        if (termo != null && !termo.isBlank()){
            return garantiaRepository.findByClienteNomeContainingIgnoreCaseOrCpfContaining(termo.trim(), termo.trim());
        }
        return garantiaRepository.findAll();
    }

    public void registrarMalUso(String codigo, String observacao){
        Garantia garantia = buscarPorCodigo(codigo);
        garantia.setMalUso(true);
        garantia.setObservacaoTecnica(observacao);
        garantiaRepository.save(garantia);
    }
}
