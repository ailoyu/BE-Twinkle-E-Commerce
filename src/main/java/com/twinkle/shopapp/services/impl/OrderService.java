package com.twinkle.shopapp.services.impl;

import com.twinkle.shopapp.configuration.VNPayConfig;
import com.twinkle.shopapp.dtos.CartItemDTO;
import com.twinkle.shopapp.dtos.OrderDTO;
import com.twinkle.shopapp.exceptions.DataNotFoundException;
import com.twinkle.shopapp.models.*;
import com.twinkle.shopapp.repositories.*;
import com.twinkle.shopapp.responses.OrderResponse;
import com.twinkle.shopapp.services.IOrderService;
import com.twinkle.shopapp.utils.EmailUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ModelMapper modelMapper;

    private final ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final DetailInputOrderRepository detailInputOrderRepository;


    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public String createOrder(OrderDTO orderDTO) throws Exception {
        // tìm xem user's id đã tồn tại chưa
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy User với id " + orderDTO.getUserId()));



        Order order = new Order();
        // Dùng Model Mapper

        // convert DTO -> Order (Nhưng ko mapping id)
        // Cài đặt ánh xạ (ko ánh xạ id của order)
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        // Bắt đầu ánh xạ (từ orderDTO -> order)
        modelMapper.map(orderDTO, order);
        order.setUser(user); // user đặt hàng
        order.setOrderDate(new Date()); // ngày đặt hàng là ngày hiện tại
        order.setStatus(OrderStatus.PENDING); // 1 đơn hàng vừa tạo ra trạng thái là PENDING

        // kIỂM TRA nếu khách hàng k nhập shipping date, lấy luôn ngày hnay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now().plusDays(3) : orderDTO.getShippingDate();

        //shippingDate phải >= ngày hôm nay
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Ngày giao hàng phải lớn hơn ngày hôm nay");
        }

        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(orderDTO.getTotalMoney());

        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems())
        {

            List<DetailInputOrder> detailInputOrders = detailInputOrderRepository.findByProductId(cartItemDTO.getProductId());
            if(!detailInputOrders.isEmpty()){
                for(DetailInputOrder detailInputOrder : detailInputOrders)
                {
                    float tolerance = 0.0001f;
                    if(Math.abs(detailInputOrder.getSize() - cartItemDTO.getSize()) < tolerance
                            && detailInputOrder.getQuantity() - cartItemDTO.getQuantity() >= 0)
                    {
                        detailInputOrder.setQuantity(detailInputOrder.getQuantity() - cartItemDTO.getQuantity());
                        detailInputOrderRepository.save(detailInputOrder);
                        break;
                    } else if(detailInputOrder.getQuantity() == 0 || detailInputOrder.getQuantity() - cartItemDTO.getQuantity() < 0)
                    {
                        throw new Exception("Size này đã không còn hàng hoặc không đủ số lượng!");
                    }
                }
            } else
            {
                throw new Exception("Không tìm thấy sản phầm này!");
            }

            // Bỏ order vào từng order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setSize(cartItemDTO.getSize());

            // lấy ra từng sản phẩm và số lượng vào trong giỏ hàng
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // Tìm thông tin từng product này có trong DB hay ko? (or sử dụng cache neu cần)
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy sản phẩm"));

            // set sản phẩm và số lượng vào trong giỏ hàng
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);



            // set giá cho từng sản phẩm
            orderDetail.setProductPrice(product
                    .getProductPrices()
                    .get(product.getProductPrices().size() - 1)
                    .getPrice());

            orderDetail.setTotalMoney(orderDetail.getProductPrice() * orderDetail.getNumberOfProducts());

            // thêm orderDetail vào danh sách
            orderDetails.add(orderDetail);
        }

        // Lưu danh sách orderDetail vào DB
        List<OrderDetail> listOrder = orderDetailRepository.saveAll(orderDetails);


        Order savedOrder = new Order();
        if(listOrder != null)
            savedOrder = orderRepository.save(order);


        Float totalMoney = order.getTotalMoney();


        if(orderDTO.getPaymentMethod().equals("Chuyển Khoản")){
            String paymenURL = getPay(totalMoney.longValue() * 1000, savedOrder.getId().intValue());
            return paymenURL;
        } else {
            String emailContent = EmailUtils.getEmailContent(order, listOrder);

            String[] recipients = {order.getEmail(), "quangtrinhhuynh02@gmail.com"};

            EmailUtils.sendEmail(recipients, "Twinkle | Đơn hàng của bạn đã được đặt thành công và đang xử lý!", emailContent);
            return "https://twinklee.netlify.app/order-detail";
        }
    }

    public String getPay(long price, Integer userId) throws UnsupportedEncodingException {

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = price*100;
        String bankCode = "NCB";

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl+"?userId="+userId);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.HOUR_OF_DAY, 48);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }


    @Override
    public Order getOrder(Long id) throws DataNotFoundException {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy đơn hàng này!"));
    }

    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public Order updateOrder(Long id, String status) throws DataNotFoundException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Ko tìm thấy order này để update"));

        order.setStatus(status);

        return orderRepository.save(order);
    }

    @Override
    @Transactional // rollback dữ liệu khi bị sai gì đó
    public void deleteOrder(Long[] ids) throws DataNotFoundException {
        for(long id : ids){
            Optional<Order> optionalorder = orderRepository.findById(id);
            if(optionalorder.isPresent()){
                orderRepository.delete(optionalorder.get()); // nếu có product trong DB ms xóa

            }
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getPendingOrders() {
        return orderRepository.findAllByStatus(OrderStatus.PENDING);
    }

    @Override
    public List<Order> getShippingOrders() {
        return orderRepository.findAllByStatus(OrderStatus.SHIPPING);
    }

    @Override
    public List<Order> getDeliveredOrders() {
        return orderRepository.findAllByStatus(OrderStatus.DELIVERED);
    }

    @Override
    public List<Order> getCancelledOrders() {
        return orderRepository.findAllByStatus(OrderStatus.CANCELLED);
    }
}
