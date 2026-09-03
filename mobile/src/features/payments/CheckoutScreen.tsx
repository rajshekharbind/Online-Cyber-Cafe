import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';

const CheckoutScreen = ({ navigation }: any) => {
  return (
    <View style={styles.container}>
      <View style={styles.card}>
        <Text style={styles.header}>Order Summary</Text>
        <View style={styles.row}>
          <Text>Application Fee</Text>
          <Text>₹100</Text>
        </View>
        <View style={styles.row}>
          <Text>Service Charge</Text>
          <Text>₹50</Text>
        </View>
        <View style={[styles.row, styles.totalRow]}>
          <Text style={styles.totalLabel}>Total Payable</Text>
          <Text style={styles.totalValue}>₹150</Text>
        </View>
      </View>

      <Text style={styles.disclaimer}>
        By clicking "Pay Now", you agree to our terms and conditions and authorize us to process your application on your behalf.
      </Text>

      <TouchableOpacity
        style={styles.payBtn}
        onPress={() => alert('Razorpay Integration Triggered')}
      >
        <Text style={styles.payBtnText}>Pay Now</Text>
      </TouchableOpacity>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, padding: 20, backgroundColor: '#f5f5f5' },
  card: { backgroundColor: '#fff', padding: 20, borderRadius: 12, elevation: 3 },
  header: { fontSize: 20, fontWeight: 'bold', marginBottom: 20 },
  row: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  totalRow: { marginTop: 15, paddingTop: 15, borderTopWidth: 1, borderTopColor: '#eee' },
  totalLabel: { fontSize: 18, fontWeight: 'bold' },
  totalValue: { fontSize: 18, fontWeight: 'bold', color: '#2e7d32' },
  disclaimer: { marginVertical: 20, color: '#666', fontSize: 12, textAlign: 'center' },
  payBtn: { backgroundColor: '#2e7d32', padding: 18, borderRadius: 8, alignItems: 'center' },
  payBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 18 }
});

export default CheckoutScreen;
