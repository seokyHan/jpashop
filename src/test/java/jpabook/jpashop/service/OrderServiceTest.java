package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static jpabook.jpashop.domain.OrderStatus.CANCEL;
import static jpabook.jpashop.domain.OrderStatus.ORDER;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;


    @DisplayName("상품을 주문한다.")
    @Test
    void orderTest() {
        // given
        Member member = createMember();
        Book book = createBook("test JPA", 20000, 10);
        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        Order getOrder = orderRepository.findOne(orderId);

        //then
        assertEquals(ORDER, getOrder.getStatus());
        assertEquals(1, getOrder.getOrderItems().size());
        assertEquals(20000 * orderCount, getOrder.getTotalPrice());
        assertEquals(8, book.getStockQuantity());

    }

    @DisplayName("주문을 취소한다.")
    @Test
    void cancelOrderTest() {
        // given
        Member member = createMember();
        Book item = createBook("bb JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);
        Order getOrder = orderRepository.findOne(orderId);

        //then
        assertEquals(CANCEL, getOrder.getStatus());
        assertEquals(10, item.getStockQuantity());
    }

    @DisplayName("주문시 수량을 초과할 경우 예외가 발생한다.")
    @Test
    void overStockQuantityTest() {
        // given
        Member member = createMember();
        Item item = createBook("aa JPA", 10000, 10);
        int orderCount = 11;

        //then
        assertThatThrownBy(() -> orderService.order(member.getId(), item.getId(), orderCount))
                .isInstanceOf(NotEnoughStockException.class)
                .hasMessage("need more stock");
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-111"));
        em.persist(member);
        return member;
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

}