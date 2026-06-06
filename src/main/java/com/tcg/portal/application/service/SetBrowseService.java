package com.tcg.portal.application.service;

import com.tcg.portal.application.port.in.SetBrowseUseCase;
import com.tcg.portal.application.port.out.SetBrowsePort;
import com.tcg.portal.domain.exception.SetNotFoundException;
import com.tcg.portal.domain.model.MagicSet;
import com.tcg.portal.domain.model.SetCardPage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetBrowseService implements SetBrowseUseCase {

    private final SetBrowsePort setBrowsePort;

    public SetBrowseService(SetBrowsePort setBrowsePort) {
        this.setBrowsePort = setBrowsePort;
    }

    @Override
    public List<MagicSet> getAllSets() {
        return setBrowsePort.findAllSets();
    }

    @Override
    public List<MagicSet> getSetsByType(String setType) {
        return setBrowsePort.findAllSets().stream()
                .filter(s -> setType.equalsIgnoreCase(s.setType()))
                .toList();
    }

    @Override
    public MagicSet getSet(String setCode) {
        return setBrowsePort.findAllSets().stream()
                .filter(s -> s.code().equalsIgnoreCase(setCode))
                .findFirst()
                .orElseThrow(() -> new SetNotFoundException(setCode));
    }

    @Override
    public SetCardPage getCardsInSet(String setCode, int page) {
        return setBrowsePort.findCardsBySetCode(setCode, page);
    }
}
