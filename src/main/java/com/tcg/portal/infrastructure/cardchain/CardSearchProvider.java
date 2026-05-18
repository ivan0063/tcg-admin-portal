package com.tcg.portal.infrastructure.cardchain;

public interface CardSearchProvider {
    ProviderResult findByName(String name);
    String providerName();
}
