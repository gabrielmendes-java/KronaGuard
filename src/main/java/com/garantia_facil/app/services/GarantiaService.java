package com.garantia_facil.app.services;

import com.garantia_facil.app.models.Garantia;
import com.garantia_facil.app.models.Usuario;
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
    public Garantia criarGarantia(Garantia garantia, Usuario usuario){
        String codigoUnico = "GAR-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        garantia.setCodigoGarantia(codigoUnico);

        LocalDate validade = garantia.getDataRealizacao().plusDays(garantia.getDiasGarantia());
        garantia.setDataValidade(validade);
        garantia.setUsuario(usuario);

        return garantiaRepository.save(garantia);
    }

    public Garantia buscarPorCodigo(String codigo, Long usuarioId){
        return garantiaRepository.findByUsuarioIdAndCodigoGarantia(usuarioId, codigo)
                .orElseThrow(() -> new RuntimeException("Garantia não encontrada."));
    }

    public Garantia buscarPorCodigo(String codigo){
        return garantiaRepository.findByCodigoGarantia(codigo)
                .orElseThrow(() -> new RuntimeException("Garantia não encontrada."));
    }

    public List<Garantia> listarOuFiltrar(String termo, Long usuarioId){
        if (termo != null && !termo.isBlank()){
            return garantiaRepository.findByUsuarioIdAndClienteNomeContainingIgnoreCaseOrUsuarioIdAndCpfContaining(usuarioId, termo.trim(), usuarioId, termo.trim());
        }
        return garantiaRepository.findByUsuarioId(usuarioId);
    }

    public void registrarMalUso(String codigo, String observacao, Long usuarioId){
        Garantia garantia = buscarPorCodigo(codigo, usuarioId);
        garantia.setMalUso(true);
        garantia.setObservacaoTecnica(observacao);
        garantiaRepository.save(garantia);
    }
}
