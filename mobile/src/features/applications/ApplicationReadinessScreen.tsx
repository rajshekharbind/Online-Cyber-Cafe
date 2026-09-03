import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';

const ApplicationReadinessScreen = ({ navigation, route }: any) => {
  const { jobId } = route.params;

  const readiness = [
    { label: 'Personal Information', status: 'COMPLETE' },
    { label: 'Educational Details', status: 'COMPLETE' },
    { label: 'Photograph', status: 'COMPLETE' },
    { label: 'Signature', status: 'COMPLETE' },
    { label: 'Category Certificate', status: 'MISSING' },
  ];

  const isReady = readiness.every(item => item.status === 'COMPLETE');

  return (
    <View style={styles.container}>
      <ScrollView style={styles.content}>
        <Text style={styles.title}>Application Readiness</Text>
        <Text style={styles.subtitle}>Please ensure all required information is provided before proceeding to payment.</Text>

        <View style={styles.list}>
          {readiness.map((item, index) => (
            <View key={index} style={styles.item}>
              <Text style={styles.label}>{item.label}</Text>
              <Text style={[styles.status, { color: item.status === 'COMPLETE' ? '#4caf50' : '#f44336' }]}>
                {item.status === 'COMPLETE' ? '✓ Complete' : '✗ Missing'}
              </Text>
            </View>
          ))}
        </View>

        {!isReady && (
          <View style={styles.warningBox}>
             <Text style={styles.warningText}>Some mandatory documents are missing. Please upload them in the Document Vault.</Text>
          </View>
        )}
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          style={[styles.btn, !isReady && styles.btnDisabled]}
          disabled={!isReady}
          onPress={() => navigation.navigate('Checkout', { jobId })}
        >
          <Text style={styles.btnText}>Proceed to Payment</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  content: { padding: 20 },
  title: { fontSize: 22, fontWeight: 'bold', marginBottom: 10 },
  subtitle: { color: '#666', marginBottom: 20 },
  list: { backgroundColor: '#f8f9fa', borderRadius: 8, padding: 15 },
  item: { flexDirection: 'row', justifyContent: 'space-between', paddingVertical: 12, borderBottomWidth: 1, borderBottomColor: '#eee' },
  label: { fontSize: 16 },
  status: { fontWeight: 'bold' },
  warningBox: { marginTop: 20, padding: 15, backgroundColor: '#fff3e0', borderRadius: 8 },
  warningText: { color: '#e65100', fontSize: 14 },
  footer: { padding: 20, borderTopWidth: 1, borderTopColor: '#eee' },
  btn: { backgroundColor: '#2196f3', padding: 16, borderRadius: 8, alignItems: 'center' },
  btnDisabled: { backgroundColor: '#bdbdbd' },
  btnText: { color: '#fff', fontWeight: 'bold', fontSize: 16 }
});

export default ApplicationReadinessScreen;
