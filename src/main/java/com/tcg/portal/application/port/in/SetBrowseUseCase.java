package com.tcg.portal.application.port.in;

import com.tcg.portal.domain.model.MagicSet;
import com.tcg.portal.domain.model.SetCardPage;

import java.util.List;

public interface SetBrowseUseCase {
    List<MagicSet> getAllSets();
    List<MagicSet> getSetsByType(String setType);
    MagicSet getSet(String setCode);
    SetCardPage getCardsInSet(String setCode, int page);
}
