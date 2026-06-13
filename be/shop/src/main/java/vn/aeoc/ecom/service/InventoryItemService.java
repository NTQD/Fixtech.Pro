package vn.aeoc.ecom.service;

import org.springframework.stereotype.Service;

import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.service.BaseService;
import vn.aeoc.ecom.dto.request.InventoryItemRequest;
import vn.aeoc.ecom.dto.response.InventoryItemDto;
import vn.aeoc.ecom.repository.InventoryItemRepository;
import vn.aeoc.entity.data.mysql.InventoryItem;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryItemService extends BaseService<InventoryItemRepository, InventoryItem, Integer> {

    public InventoryItemService(InventoryItemRepository repository) {
        super(repository);
    }

    public Page<InventoryItemDto> getInventoryItems(String keyword, Pageable pageable) {
        Page<InventoryItem> page = pageQuery(pageable,
                () -> repository.getByCriteria(keyword, pageable),
                () -> repository.countByCriteria(keyword));

        List<InventoryItemDto> dtos = page.getItems().stream()
                .map(InventoryItemDto::fromEntity)
                .collect(Collectors.toList());

        return new Page<>(pageable, dtos);
    }

    public InventoryItemDto getInventoryItem(Integer id) {
        InventoryItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }
        return InventoryItemDto.fromEntity(item);
    }

    public InventoryItemDto createInventoryItem(InventoryItemRequest request) {
        InventoryItem item = new InventoryItem();
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setStock(request.getStock() != null ? request.getStock() : 0);
        item.setImageUrl(request.getImageUrl());

        return InventoryItemDto.fromEntity(insert(item));
    }

    public InventoryItemDto updateInventoryItem(Integer id, InventoryItemRequest request) {
        InventoryItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }
        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setStock(request.getStock());
        item.setImageUrl(request.getImageUrl());

        return InventoryItemDto.fromEntity(update(id, item));
    }

    public Integer deleteInventoryItem(Integer id) {
        InventoryItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("Item not found");
        }
        return delete(id);
    }
}
