package com.assetco.hotspots.optimization;

import com.assetco.search.results.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.assetco.search.results.AssetVendorRelationshipLevel.*;
import static com.assetco.search.results.HotspotKey.*;

// This code manages filling the showcase if it's not already set
// it make sure the first partner-lvl vendor with enough assets on the page gets the showcase
class RelationshipBasedOptimizer {
    public void optimize(SearchResults searchResults) {
        Iterator<Asset> iterator = searchResults.getFound().iterator();
        var showcaseFull = searchResults.getHotspot(Showcase).getMembers().size() > 0;
        var partnerAssets = new ArrayList<Asset>();
        var goldAssets = new ArrayList<Asset>();
        var silverAssets = new ArrayList<Asset>();
        Map<String, List<Asset>> showcase = new HashMap<>();

        while (iterator.hasNext()) {
            Asset asset = iterator.next();
            // HACK! trap gold and silver assets for use later
            if (asset.getVendor().getRelationshipLevel() == Gold)
                goldAssets.add(asset);
            else if (asset.getVendor().getRelationshipLevel() == Silver)
                silverAssets.add(asset);

            if (asset.getVendor().getRelationshipLevel() != Partner)
                continue;

            // remember this partner asset
            partnerAssets.add(asset);
            var assetsByVendor = showcase.getOrDefault(asset.getVendor().getId(), new ArrayList<>());
            assetsByVendor.add(asset);
            showcase.put(asset.getVendor().getId(), assetsByVendor);

        }

        List<Asset> showcaseEligibleAssets =  showcase.values().stream().filter(l -> l.size() >= 3).findFirst().orElseGet(ArrayList::new);

        // if too many assets in showcase - put in top picks instead...
        List<Asset> showcaseAssets = showcaseEligibleAssets.stream().limit(5).collect(Collectors.toList());
        showcaseEligibleAssets.stream().skip(4).forEach(a -> searchResults.getHotspot(TopPicks).addMember(a));

        // todo - this does not belong here!!!
        var highValueHotspot = searchResults.getHotspot(HighValue);
        for (var asset : partnerAssets)
            if (!highValueHotspot.getMembers().contains(asset))
                highValueHotspot.addMember(asset);

        // TODO - this needs to be moved to something that only manages the fold
        for (var asset : partnerAssets)
            searchResults.getHotspot(Fold).addMember(asset);

        // only copy showcase assets into hotspot if there are enough for a partner to claim the showcase
        if (!showcaseFull && showcaseAssets.size() >= 3) {
            Hotspot showcaseHotspot = searchResults.getHotspot(Showcase);
            for (Asset asset : showcaseAssets)
                showcaseHotspot.addMember(asset);
        }

        // acw-14339: gold assets should be in high value hotspots if there are no partner assets in search
        for (var asset : goldAssets)
            if (!highValueHotspot.getMembers().contains(asset))
                highValueHotspot.addMember(asset);

        // acw-14341: gold assets should appear in fold box when appropriate
        for (var asset : goldAssets)
            searchResults.getHotspot(Fold).addMember(asset);

        // LOL acw-14511: gold assets should appear in fold box when appropriate
        for (var asset : silverAssets)
            searchResults.getHotspot(Fold).addMember(asset);
    }
}
